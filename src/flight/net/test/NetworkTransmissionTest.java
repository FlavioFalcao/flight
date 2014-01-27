package flight.net.test;

import java.io.IOException;
import java.util.LinkedList;

import flight.net.Client;
import flight.net.Server;
import flight.net.err.TransmissionException;
import flight.net.msg.Message;
import flight.net.msg.MessageHandler;
import flight.net.msg.StringMessage;

/**
 * A unit test for basic network transmission. Requires successful message
 * serialization...
 * 
 * @see MessageSerializationTest
 * 
 * @author Colby Horn
 */
public class NetworkTransmissionTest {

	/**
	 * Attempts to transmit and receive networking messages through connected
	 * start {@link Client} and {@link Server} instances. Unsuccessful message
	 * recoveries are shown to aid debugging.
	 */
	public static void main(String[] args) throws InterruptedException,
			IOException, TransmissionException {

		Client client1 = new Client(), client2 = new Client();
		MessageRecorder record1 = new MessageRecorder(), record2 = new MessageRecorder();
		client1.addMessageHandler(record1);
		client2.addMessageHandler(record2);
		Server server = new Server();

		server.start();
		Thread.sleep(500);

		client1.connect();
		client2.connect();
		Thread.sleep(500);

		StringMessage message1 = new StringMessage(client1.getId(),
				"HelloWorld from Client1!"), message2 = new StringMessage(
				client2.getId(), "HelloWorld from Client2!");
		client1.send(message1);
		client2.send(message2);
		Thread.sleep(500);

		client1.disconnect();
		client2.disconnect();
		server.stop();

		Thread.sleep(500);
		if (record2.size() != 1 || !record2.peek().equals(message1)) {
			System.out.println("failed recovery: " + message1);
			System.out.println("  received instead: " + record2.peek());
		} else {
			System.out.println("sucessfully recovery: " + record2.peek());
		}
		if (record1.size() != 1 || !record1.peek().equals(message2)) {
			System.out.println("failed recovery: " + message2);
			System.out.println("  received instead: " + record1.peek());
		} else {
			System.out.println("sucessfully recovery: " + record1.peek());
		}

		System.exit(0);

	}

	@SuppressWarnings("serial")
	private static class MessageRecorder extends LinkedList<Message> implements
			MessageHandler {

		@Override
		public void handleMessage(Message message) {
			add(message);
		}

	}

}
