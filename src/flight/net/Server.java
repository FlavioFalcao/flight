package flight.net;

import static flight.global.Const.SERVER_ADDED_CLIENT;
import static flight.global.Const.SERVER_CONNECTION_LOST;
import static flight.global.Const.SERVER_REMOVED_CLIENT;
import static flight.global.Const.SERVER_STARTED;
import static flight.global.Const.SERVER_STOPPED;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import flight.global.Logger;
import flight.net.err.TransmissionException;
import flight.net.msg.AcknowledgeMessage;
import flight.net.msg.DataMessage;
import flight.net.msg.EndTransmissionMessage;
import flight.net.msg.Message;
import flight.net.msg.MessageProducer;
import flight.net.msg.MessageReader;
import flight.net.msg.MessageWriter;
import flight.net.msg.NullMessage;
import flight.net.msg.SetClientIDMessage;
import flight.net.msg.StartTransmissionMessage;
import flight.net.msg.StringMessage;

public class Server extends MessageProducer implements Runnable {

	public Server() {}

	public Server(int hostPort) {
		this.hostPort = hostPort;
	}

	private int								hostPort	= 5139;

	private Set<Class<? extends Message>>	caughtMessages,
			rebroadcastMessages;

	{
		caughtMessages = new LinkedHashSet<Class<? extends Message>>();
		caughtMessages.add(NullMessage.class);
		caughtMessages.add(StartTransmissionMessage.class);
		caughtMessages.add(EndTransmissionMessage.class);
		caughtMessages.add(AcknowledgeMessage.class);
		caughtMessages.add(SetClientIDMessage.class);

		rebroadcastMessages = new LinkedHashSet<Class<? extends Message>>();
		rebroadcastMessages.add(DataMessage.class);
		rebroadcastMessages.add(StringMessage.class);
	}

	private ServerSocket					server;
	private ExecutorService					threadPool;
	private boolean							running		= false;

	private Map<Byte, ClientConnection>		clients;

	private void initServer() throws IOException {
		server = new ServerSocket(hostPort);
		threadPool = Executors.newCachedThreadPool();
		clients = new ConcurrentSkipListMap<Byte, ClientConnection>();
		running = true;
		Logger.logOutput(SERVER_STARTED, hostPort);
	}

	private void addClient(ClientConnection client) throws IOException {
		client.id = 0;
		synchronized (clients) {
			while (clients.containsKey(++client.id));
			client.write(new SetClientIDMessage((byte) 0,
					client.id));
			clients.put(client.id, client);
		}
	}

	private void removeClient(byte id) {
		ClientConnection client;
		synchronized (clients) {
			client = clients.remove(id);
		}
		if (client != null) {
			try {
				client.write(new EndTransmissionMessage((byte) 0));
				client.clientConnection.close();
			} catch (NullPointerException | IOException e) {}
			Logger.logOutput(SERVER_REMOVED_CLIENT, id);
		}
	}

	private void handleMessage(Message message) throws IOException {
		if (caughtMessages.contains(message.getClass())) {
			if (message instanceof EndTransmissionMessage) {
				removeClient(message.getSource());
			}
		} else if (rebroadcastMessages.contains(message.getClass())) {
			rebroadcastMessage(message);
		} else {
			broadcastMessage(message);
		}
	}

	private void rebroadcastMessage(Message message) throws IOException {
		for (Entry<Byte, ClientConnection> entry : clients.entrySet())
			if (entry.getKey() != message.getSource()) {
				entry.getValue().write(message);
			}
	}

	private class ClientConnection implements Runnable {

		public ClientConnection(Socket clientConnection) {
			this.clientConnection = clientConnection;
		}

		public Socket			clientConnection;
		private MessageWriter	outputToClient;
		public MessageReader	inputFromClient;

		public byte				id;
		public boolean			connected	= false;

		private void initClientConnection() throws IOException,
				InstantiationException, IllegalAccessException,
				TransmissionException {
			// initializing networking objects
			outputToClient = new MessageWriter(
					clientConnection.getOutputStream());
			inputFromClient = new MessageReader(
					clientConnection.getInputStream());

			// read and verify transmission header from client
			Message start = inputFromClient.read();
			if (!start.equals(new StartTransmissionMessage((byte) -1)))
				throw new TransmissionException("Connection not accepted");

			// send transmission header and initialize client connection state
			outputToClient.write(new StartTransmissionMessage((byte) 0));
			addClient(this);
			connected = true;
			Logger.logOutput(SERVER_ADDED_CLIENT, id, clientConnection
					.getInetAddress().getCanonicalHostName());
		}

		@Override
		public void run() {
			try {
				initClientConnection();
				while (connected) {
					handleMessage(inputFromClient.read());
				}
			} catch (InstantiationException | IllegalAccessException
					| IOException | TransmissionException e) {
				Logger.logError(SERVER_CONNECTION_LOST, id);
			}
			removeClient(id);
		}

		public void write(Message message) throws IOException {
			synchronized (outputToClient) {
				outputToClient.write(message);
			}
		}

	}

	public void stop() {
		if (running) {
			running = false;
			for (Byte id : clients.keySet())
				removeClient(id);
			Logger.logOutput(SERVER_STOPPED);
		} else
			running = false;
	}

	public void runWithExceptions() throws IOException {
		initServer();
		while (running) {
			final Socket clientConnection = server.accept();
			threadPool.execute(new ClientConnection(clientConnection));
		}
		stop();
	}

	@Override
	public void run() {
		try {
			runWithExceptions();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		Server server = new Server();
		server.runWithExceptions();
	}

}
