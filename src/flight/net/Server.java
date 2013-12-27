package flight.net;

import static flight.global.Const.SERVER_ADDED_CLIENT;
import static flight.global.Const.SERVER_CONNECTION_FAILED;
import static flight.global.Const.SERVER_CONNECTION_LOST;
import static flight.global.Const.SERVER_MESSAGE_RECEIVED;
import static flight.global.Const.SERVER_REMOVED_CLIENT;
import static flight.global.Const.SERVER_STARTED;
import static flight.global.Const.SERVER_STOPPED;
import static flight.global.Const.SERVER_SYNC_NOT_FOUND;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import flight.global.Logger;
import flight.net.err.SyncNotFoundException;
import flight.net.msg.AddSyncMessage;
import flight.net.msg.AssignClientIDMessage;
import flight.net.msg.DataMessage;
import flight.net.msg.EndTransmissionMessage;
import flight.net.msg.Message;
import flight.net.msg.MessageReader;
import flight.net.msg.MessageWriter;
import flight.net.msg.RemoveSyncMessage;
import flight.net.msg.StringMessage;
import flight.net.msg.UpdateSyncMessage;
import flight.net.syn.Sync;

public class Server extends Host {

	public Server() {}

	public Server(int serverPort) {
		this.serverPort = serverPort;
	}

	private int				serverPort	= 5139;

	private ServerSocket	server;
	private ExecutorService	threadPool;
	private boolean			running		= false;

	{
		setId((byte) 0);
	}

	public void start() throws IOException {
		threadPool = Executors.newCachedThreadPool();
		clients = new ConcurrentSkipListMap<Byte, ClientConnection>();
		server = new ServerSocket(serverPort);
		connectionReceiver.start();
		running = true;
		Logger.logOutput(SERVER_STARTED, serverPort);
	}

	public void stop() {
		running = false;
		try {
			server.close();
		} catch (IOException e) {}
		for (ClientConnection client : clients.values())
			client.disconnect();
		threadPool.shutdown();
		Logger.logOutput(SERVER_STOPPED);
	}

	private Map<Byte, ClientConnection>		clients;

	private Set<Class<? extends Message>>	rebroadcastMessages;
	{
		rebroadcastMessages = new LinkedHashSet<Class<? extends Message>>();
		rebroadcastMessages.add(DataMessage.class);
		rebroadcastMessages.add(StringMessage.class);
	}

	private class ClientConnection {

		public ClientConnection(Socket clientConnection) {
			this.clientConnection = clientConnection;
		}

		private byte	id			= 0;
		private boolean	connected	= false;

		public void connect() throws IOException {
			// initializing networking objects
			outputToClient = new MessageWriter(
					clientConnection.getOutputStream());
			inputFromClient = new MessageReader(
					clientConnection.getInputStream());

			// mark client as connected
			connected = true;

			// initialize client state
			synchronized (clients) {
				while (clients.containsKey(++id));
				send(new AssignClientIDMessage(getId(), id));
				clients.put(id, this);
			}
			for (Sync sync : registry)
				send(new AddSyncMessage(getId(), sync));
			threadPool.execute(messageReceiver);
			Logger.logOutput(SERVER_ADDED_CLIENT, id, clientConnection
					.getInetAddress().getCanonicalHostName());
		}

		public void disconnect() {
			connected = false;
			clients.remove(id);
			try {
				send(new EndTransmissionMessage(getId()));
				if (!clientConnection.isClosed())
					clientConnection.close();
			} catch (IOException e) {}
			Logger.logOutput(SERVER_REMOVED_CLIENT, id);
			for (Sync sync : registry) {
				if (sync.getClientId() == id)
					rebroadcastMessage(new RemoveSyncMessage(getId(),
							sync.getId()));
			}
		}

		private Socket			clientConnection;
		private MessageWriter	outputToClient;
		private MessageReader	inputFromClient;

		public void send(Message message) {
			if (connected) {
				try {
					synchronized (outputToClient) {
						outputToClient.write(message);
					}
				} catch (IOException e) {
					Logger.logError(SERVER_CONNECTION_LOST, id);
					disconnect();
				}
			}
		}

		private Runnable	messageReceiver;
		{
			messageReceiver = new Runnable() {
				@Override
				public void run() {
					try {
						while (connected) {
							Message message = inputFromClient.read();
							Logger.logOutput(SERVER_MESSAGE_RECEIVED, message);
							if (caughtMessages.contains(message.getClass()))
								handleMessage(message);
							else if (rebroadcastMessages.contains(message
									.getClass()))
								rebroadcastMessage(message);
							else
								messager.broadcast(message);
						}
					} catch (IOException e) {
						if (connected) {
							Logger.logError(SERVER_CONNECTION_LOST, id);
							disconnect();
						}
					}
				}
			};
		}

	}

	private void handleMessage(Message message) throws IOException {
		if (message instanceof EndTransmissionMessage) {
			ClientConnection client = clients.get(message.getSource());
			if (client != null)
				client.disconnect();
		} else if (message instanceof AddSyncMessage) {
			AddSyncMessage addMessage = (AddSyncMessage) message;
			registry.add(addMessage.getSync());
			rebroadcastMessage(addMessage);
		} else if (message instanceof UpdateSyncMessage) {
			UpdateSyncMessage updateMessage = (UpdateSyncMessage) message;
			try {
				registry.update(updateMessage.getSyncId(),
						updateMessage.getSyncData());
				rebroadcastMessage(message);
			} catch (SyncNotFoundException e) {
				Logger.logError(SERVER_SYNC_NOT_FOUND,
						updateMessage.getSyncId());
			}
		} else if (message instanceof RemoveSyncMessage) {
			registry.remove(((RemoveSyncMessage) message).getSyncId());
			rebroadcastMessage(message);
		}
	}

	private void rebroadcastMessage(Message message) {
		for (ClientConnection client : clients.values())
			if (client.id != message.getSource())
				client.send(message);
	}

	Thread	connectionReceiver;
	{
		connectionReceiver = new Thread() {
			@Override
			public void run() {
				while (running) {
					ClientConnection client = null;
					try {
						client = new ClientConnection(server.accept());
						client.connect();
					} catch (IOException e) {
						if (running) {
							if (client != null)
								client.disconnect();
							Logger.logOutput(SERVER_CONNECTION_FAILED);
						}
					}
				}
			}
		};
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		Server server = new Server();
		server.start();
	}

}
