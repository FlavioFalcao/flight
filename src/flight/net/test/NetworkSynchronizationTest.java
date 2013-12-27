package flight.net.test;

import java.io.IOException;

import flight.net.Client;
import flight.net.Server;
import flight.net.err.TransmissionException;
import flight.net.syn.IntSync;
import flight.net.syn.Sync;

public class NetworkSynchronizationTest {

	public static void main(String[] args) throws InterruptedException,
			IOException, TransmissionException {

		Client client1 = new Client(), client2 = new Client();
		Server server = new Server();

		server.start();
		Thread.sleep(500);

		client1.connect();
		Thread.sleep(500);
		IntSync num1 = new IntSync(11), num2 = new IntSync(13), num3 = new IntSync(
				22);
		client1.registry().register(num1);
		client1.registry().register(num2);
		Thread.sleep(500);

		client2.connect();
		Thread.sleep(500);
		client2.registry().register(num3);
		Thread.sleep(500);

		num1.value(15);;
		client1.registry().remove(num2.getId());
		Thread.sleep(500);

		for (Sync sync1 : client2.registry()) {
			Sync sync2 = client1.registry().get(sync1.getId());
			if (!sync1.equals(sync2)) {
				System.out.println("failed recovery: " + sync1);
				System.out.println("  received instead: " + sync2);
			} else {
				System.out.println("sucessful recovery: " + sync2);
			}
		}

		client1.disconnect();
		client2.disconnect();
		server.stop();
		System.exit(0);

	}

}
