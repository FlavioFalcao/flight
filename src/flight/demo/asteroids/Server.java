package flight.demo.asteroids;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {

	public Server() {
		flight = new flight.net.Server();
	}

	public Server(int serverPort) {
		flight = new flight.net.Server(serverPort);
	}

	flight.net.Server	flight;

	private static long	UPDATES_PER_SECOND	= 10;
	private static long	UPDATE_DURATION		= 1000 / UPDATES_PER_SECOND;

	@Override
	public void run() {
		try {
			flight.start();
			init();
			while (true) {
				update();
				Thread.sleep(UPDATE_DURATION);
			}
		} catch (IOException e) {
			System.out
					.println("error: flight engine server could not be started");
		} catch (InterruptedException e) {}
	}

	private static final int	ASTEROIDS			= 20;
	public static final float	AST_MAX_SPEED		= 2;
	public static final float	AST_MIN_SIZE		= 10;
	public static final float	AST_MAX_SIZE		= 40;
	public static final float	AST_MAX_ROTATION	= (float) (Math.PI / 64);

	private Random				random				= new Random();
	private List<Asteroid>		asteroids			= new CopyOnWriteArrayList<Asteroid>();

	private void init() {
		for (int i = 0; i < ASTEROIDS; ++i) {
			Asteroid asteroid = new Asteroid(random.nextFloat() * Client.WIDTH,
					random.nextFloat() * Client.HEIGHT,
					(float) (random.nextFloat() * (2 * Math.PI)),
					random.nextFloat() * (AST_MAX_SIZE - AST_MIN_SIZE)
							+ AST_MIN_SIZE, random.nextFloat()
							* (2 * AST_MAX_ROTATION) - AST_MAX_ROTATION);
			asteroid.forward(random.nextFloat() * AST_MAX_SPEED);
			asteroids.add(asteroid);
			asteroid.register(flight.registry());
		}
	}

	private void update() {
		for (Asteroid asteroid : asteroids)
			asteroid.update();
	}

	public static void main(String args[]) {
		Server asteroidsServer;
		if (args.length >= 1)
			asteroidsServer = new Server(Integer.parseInt(args[0]));
		else
			asteroidsServer = new Server();
		asteroidsServer.run();
	}

}
