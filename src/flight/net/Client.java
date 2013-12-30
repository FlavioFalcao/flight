package flight.net;

import static flight.global.Const.CLIENT_CONNECTING;
import static flight.global.Const.CLIENT_CONNECTION_LOST;
import static flight.global.Const.CLIENT_MESSAGE_RECEIVED;
import static flight.global.Const.CLIENT_STARTED;
import static flight.global.Const.CLIENT_STOPPED;
import static flight.global.Const.CLIENT_SYNC_NOT_FOUND;
import static flight.global.Const.CLIENT_MESSAGE_SENT;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import flight.global.Logger;
import flight.net.err.SyncNotFoundException;
import flight.net.err.TransmissionException;
import flight.net.msg.AddSyncMessage;
import flight.net.msg.AssignClientIDMessage;
import flight.net.msg.EndTransmissionMessage;
import flight.net.msg.Message;
import flight.net.msg.MessageReader;
import flight.net.msg.MessageWriter;
import flight.net.msg.RemoveSyncMessage;
import flight.net.msg.UpdateSyncMessage;
import flight.net.syn.Sync;
import flight.net.syn.SyncRegistryHost;

public class Client extends Host {

	public Client() {}

	public Client(String serverName) {
		this.serverName = serverName;
	}

	public Client(String serverName, int serverPort) {
		this(serverName);
		this.serverPort = serverPort;
	}

	private String	serverName	= "localhost";
	private int		serverPort	= 5139;

	private boolean	connected	= false;

	public void connect() throws IOException, TransmissionException {
		connect(serverName, serverPort);
	}

	public void connect(String serverName, int serverPort) throws IOException,
			TransmissionException {
		// initialize networking objects
		Logger.logOutput(CLIENT_CONNECTING, serverName, serverPort);
		serverConnection = new Socket(serverName, serverPort);
		outputToServer = new MessageWriter(serverConnection.getOutputStream());
		inputFromServer = new MessageReader(serverConnection.getInputStream());

		// read and verify transmission header from server
		Message assignMessage = inputFromServer.read();
		if (!(assignMessage instanceof AssignClientIDMessage))
			throw new TransmissionException("Connection not accepted");

		// initialize client state
		handleMessage(assignMessage);
		connected = true;
		messageReceiver.start();
		Logger.logOutput(CLIENT_STARTED, getId());
	}

	public void disconnect() {
		connected = false;
		clearId();
		try {
			serverConnection.close();
		} catch (IOException e) {}
		try {
			messageReceiver.join();
		} catch (InterruptedException e) {}
		Logger.logOutput(CLIENT_STOPPED);
	}

	private Socket			serverConnection;
	private MessageWriter	outputToServer;
	private MessageReader	inputFromServer;

	public void send(Message message) {
		if (connected) {
			try {
				synchronized (outputToServer) {
					outputToServer.write(message);
				}
				Logger.logOutput(CLIENT_MESSAGE_SENT, message);
			} catch (IOException e) {
				Logger.logError(CLIENT_CONNECTION_LOST);
				disconnect();
			}
		}
	}

	private Thread	messageReceiver;
	{
		messageReceiver = new Thread() {
			@Override
			public void run() {
				try {
					while (connected) {
						Message message = inputFromServer.read();
						Logger.logOutput(CLIENT_MESSAGE_RECEIVED, message);
						if (caughtMessages.contains(message.getClass()))
							handleMessage(message);
						else
							messager.broadcast(message);
					}
				} catch (IOException e) {
					if (connected) {
						Logger.logError(CLIENT_CONNECTION_LOST);
						disconnect();
					}
				}
			}
		};
	}

	private void handleMessage(Message message) {
		if (message instanceof EndTransmissionMessage) {
			disconnect();
		} else if (message instanceof AssignClientIDMessage) {
			setId(((AssignClientIDMessage) message).getNewId());
		} else if (message instanceof AddSyncMessage) {
			AddSyncMessage addMessage = (AddSyncMessage) message;
			registry.add(addMessage.getSync());
			messager.broadcast(addMessage);
		} else if (message instanceof UpdateSyncMessage) {
			UpdateSyncMessage updateMessage = (UpdateSyncMessage) message;
			try {
				registry.update(updateMessage.getSyncId(),
						updateMessage.getSyncData());
			} catch (SyncNotFoundException e) {
				Logger.logError(CLIENT_SYNC_NOT_FOUND,
						updateMessage.getSyncId());
			}
		} else if (message instanceof RemoveSyncMessage) {
			messager.broadcast(message);
			registry.remove(((RemoveSyncMessage) message).getSyncId());
		}

	}

	{
		registryListener = new SyncRegistryHost() {
			@Override
			public byte getHostId() {
				return getId();
			}

			@Override
			public void syncRegistered(Sync sync) {
				if (sync.getClientId() == getId())
					send(new AddSyncMessage(getId(), sync));
			}

			@Override
			public void syncUpdated(Sync sync) {
				send(new UpdateSyncMessage(getId(), sync));
			}

			@Override
			public void syncRemoved(Sync sync) {
				if (sync.getClientId() == getId())
					send(new RemoveSyncMessage(getId(), sync));
			}
		};
		registry.addRegistryListener(registryListener);
	}

	public static void main(String[] args) throws UnknownHostException,
			InstantiationException, IllegalAccessException, IOException,
			TransmissionException {
		Client client = new Client();
		client.connect();
	}

}
