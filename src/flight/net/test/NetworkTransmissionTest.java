package flight.net.test;

import java.util.LinkedList;

import flight.net.Client;
import flight.net.Server;
import flight.net.msg.Message;
import flight.net.msg.MessageHandler;
import flight.net.msg.StringMessage;

public class NetworkTransmissionTest {

	public static void main(String[] args) throws InterruptedException {

		Client client1 = new Client(), client2 = new Client();
		MessageRecorder record1 = new MessageRecorder(), record2 = new MessageRecorder();
		client1.addMessageHandler(record1);
		client2.addMessageHandler(record2);
		Server server = new Server();

		new Thread(server).start();
		Thread.sleep(500);
		new Thread(client1).start();
		new Thread(client2).start();

		Thread.sleep(500);
		StringMessage message1 = new StringMessage(client1.getId(),
				"HelloWorld from Client1!"), message2 = new StringMessage(
				client2.getId(), "HelloWorld from Client2!");
		client1.sendMessage(message1);
		client2.sendMessage(message2);

		Thread.sleep(500);
		client1.stop();
		client2.stop();
		server.stop();

		Thread.sleep(500);
		if (record2.size() != 1 || !record2.peek().equals( message1)) {
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
