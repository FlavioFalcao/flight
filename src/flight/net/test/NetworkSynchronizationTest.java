package flight.net.test;

import flight.net.Client;
import flight.net.Server;
import flight.net.syn.IntSync;
import flight.net.syn.Sync;

public class NetworkSynchronizationTest {

	public static void main(String[] args) throws InterruptedException {

		Client client1 = new Client(), client2 = new Client();
		// MessageRecorder record1 = new MessageRecorder(), record2 = new
		// MessageRecorder();
		// client1.addMessageHandler(record1);
		// client2.addMessageHandler(record2);
		Server server = new Server();

		new Thread(server).start();
		Thread.sleep(500);

		new Thread(client1).start();
		Thread.sleep(500);
		IntSync num1 = new IntSync(11), num2 = new IntSync(13), num3 = new IntSync(
				22);
		client1.addSync(num1);
		client1.addSync(num2);
		Thread.sleep(500);

		new Thread(client2).start();
		Thread.sleep(500);
		client2.addSync(num3);
		Thread.sleep(500);

		client1.removeSync(num2.getId());
		Thread.sleep(500);

		for (Sync sync1 : client2.getSyncs()) {
			Sync sync2 = client1.getSync(sync1.getId());
			if (!sync1.equals(sync2)) {
				System.out.println("failed recovery: " + sync1);
				System.out.println("  received instead: " + sync2);
			} else {
				System.out.println("sucessful recovery: " + sync2);
			}
		}

		client1.stop();
		client2.stop();
		server.stop();
		System.exit(0);

	}

	// @SuppressWarnings("serial")
	// private static class MessageRecorder extends LinkedList<Message>
	// implements
	// MessageHandler {
	//
	// @Override
	// public void handleMessage(Message message) {
	// add(message);
	// }
	//
	// }

}
