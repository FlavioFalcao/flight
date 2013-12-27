package flight.net.test;

import java.io.IOException;
import java.io.Serializable;

import flight.net.Client;
import flight.net.Server;
import flight.net.err.TransmissionException;
import flight.net.syn.BooleanSync;
import flight.net.syn.ByteSync;
import flight.net.syn.CharSync;
import flight.net.syn.DoubleSync;
import flight.net.syn.FloatSync;
import flight.net.syn.IntSync;
import flight.net.syn.LongSync;
import flight.net.syn.ObjectSync;
import flight.net.syn.ShortSync;
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
		ByteSync byteSync = new ByteSync((byte) 4);
		ShortSync shortSync = new ShortSync((byte) 8);
		IntSync intSync = new IntSync(15);
		LongSync longSync = new LongSync(16);
		FloatSync floatSync = new FloatSync(23);
		DoubleSync doubleSync = new DoubleSync(42);
		client1.registry().register(byteSync);
		client1.registry().register(shortSync);
		client1.registry().register(intSync);
		client1.registry().register(longSync);
		client1.registry().register(floatSync);
		client1.registry().register(doubleSync);
		Thread.sleep(500);

		client2.connect();
		Thread.sleep(500);
		BooleanSync boolSync = new BooleanSync(true);
		CharSync charSync = new CharSync('c');
		ObjectSync<Coordinate> objSync = new ObjectSync<Coordinate>(
				new Coordinate(10, 5));
		client2.registry().register(boolSync);
		client2.registry().register(charSync);
		client2.registry().register(objSync);
		Thread.sleep(500);

		charSync.value('h');
		objSync.value(new Coordinate(10, 4));
		client1.registry().remove(doubleSync);
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

	@SuppressWarnings("serial")
	private static class Coordinate implements Serializable {
		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		int	x, y;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Coordinate)
				return x == ((Coordinate) obj).x && y == ((Coordinate) obj).y;
			else
				return false;
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

}
