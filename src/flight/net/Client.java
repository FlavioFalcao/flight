package flight.net;

import static flight.global.Const.CLIENT_CONNECTING;
import static flight.global.Const.CLIENT_STARTED;
import static flight.global.Const.CLIENT_HANDLING_MESSAGE;
import static flight.global.Const.CLIENT_CONNECTION_LOST;
import static flight.global.Const.CLIENT_STOPPED;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Set;

import flight.global.Logger;
import flight.net.err.TransmissionException;

public class Client extends MessageProducer implements Runnable {

	public Client() {}

	public Client(String hostName) {
		this.hostName = hostName;
	}

	public Client(String hostName, int hostPort) {
		this(hostName);
		this.hostPort = hostPort;
	}

	private String							hostName	= "localhost";
	private int								hostPort	= 5139;

	private Set<Class<? extends Message>>	caughtMessages;

	{
		caughtMessages = new LinkedHashSet<Class<? extends Message>>();
		caughtMessages.add(NullMessage.class);
		caughtMessages.add(StartTransmissionMessage.class);
		caughtMessages.add(EndTransmissionMessage.class);
		caughtMessages.add(AcknowledgeMessage.class);
		caughtMessages.add(SetClientIDMessage.class);
	}

	private byte							id;
	private boolean							running		= false;

	private Socket							serverConnection;
	private MessageWriter					outputToServer;
	private MessageReader					inputFromServer;

	private void initServerConnection() throws IOException,
			InstantiationException, IllegalAccessException,
			TransmissionException {
		// initialize networking objects
		Logger.logOutput(CLIENT_CONNECTING, hostName, hostPort);
		serverConnection = new Socket(hostName, hostPort);
		outputToServer = new MessageWriter(serverConnection.getOutputStream());
		inputFromServer = new MessageReader(serverConnection.getInputStream());
		outputToServer.write(new StartTransmissionMessage());

		// read and verify transmission header from server
		Message start = inputFromServer.read(), assignment = inputFromServer
				.read();
		if (!start.equals(new StartTransmissionMessage((byte) 0))
				|| !(assignment instanceof SetClientIDMessage))
			throw new TransmissionException("Connection not accepted");

		// initialize client state
		id = ((SetClientIDMessage) assignment).getNewId();
		running = true;
		Logger.logOutput(CLIENT_STARTED, id);
	}

	private void handleMessage(Message message) {
		Logger.logOutput(CLIENT_HANDLING_MESSAGE, message);
		if (caughtMessages.contains(message.getClass())) {
			if (message instanceof EndTransmissionMessage) {
				running = false;
			} else if (message instanceof SetClientIDMessage) {
				id = ((SetClientIDMessage) message).getNewId();
			}
		} else {
			broadcastMessage(message);
		}
	}

	public void stop() {
		if (running) {
			running = false;
			try {
				serverConnection.close();
			} catch (IOException e) {}
			Logger.logOutput(CLIENT_STOPPED);
		} else
			running = false;
	}

	public void runWithExceptions() throws UnknownHostException, IOException,
			InstantiationException, IllegalAccessException,
			TransmissionException {
		initServerConnection();
		try {
			while (running)
				handleMessage(inputFromServer.read());
		} catch (IOException e) {
			if (running)
				Logger.logError(CLIENT_CONNECTION_LOST);
		}
		stop();
	}

	@Override
	public void run() {
		try {
			runWithExceptions();
		} catch (InstantiationException | IllegalAccessException | IOException
				| TransmissionException e) {
			stop();
		}
	}

	public void sendMessage(Message message) {
		if (running) {
			try {
				synchronized (outputToServer) {
					outputToServer.write(message);
				}
			} catch (IOException e) {
				Logger.logError(CLIENT_CONNECTION_LOST);
				stop();
			}
		}
	}

	public byte getId() {
		if (running)
			return id;
		else
			return -1;
	}

	public static void main(String[] args) throws UnknownHostException,
			InstantiationException, IllegalAccessException, IOException,
			TransmissionException {
		Client client = new Client();
		client.runWithExceptions();
	}

}