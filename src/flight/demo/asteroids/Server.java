package flight.demo.asteroids;

import static flight.demo.asteroids.Client.HEIGHT;
import static flight.demo.asteroids.Client.WIDTH;
import static flight.demo.asteroids.Client.spaceObjFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import flight.net.syn.ObjectSync;
import flight.net.syn.Sync;

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
	private List<Asteroid>		asteroids			= new ArrayList<Asteroid>();

	private void init() {
		for (int i = 0; i < ASTEROIDS; ++i) {
			Asteroid asteroid = new Asteroid(random.nextFloat()
					* (AST_MAX_SIZE - AST_MIN_SIZE) + AST_MIN_SIZE,
					random.nextFloat() * (2 * AST_MAX_ROTATION)
							- AST_MAX_ROTATION);
			asteroid.spawn(random.nextFloat() * WIDTH, random.nextFloat()
					* HEIGHT, (float) (random.nextFloat() * (2 * Math.PI)));
			asteroid.forward(random.nextFloat() * AST_MAX_SPEED);
			asteroids.add(asteroid);
			asteroid.register(flight.registry());
		}
	}

	@SuppressWarnings("unchecked")
	private void update() {
		for (Asteroid asteroid : asteroids) {
			if (asteroid.isAlive()) {
				asteroid.update();
				if (asteroid.getX() < 0 || WIDTH < asteroid.getX()) {
					float mod = asteroid.getX() % WIDTH;
					asteroid.setX(0 < mod ? mod : mod + WIDTH);
				}
				if (asteroid.getY() < 0 || HEIGHT < asteroid.getY()) {
					float mod = asteroid.getY() % HEIGHT;
					asteroid.setY(0 < mod ? mod : mod + HEIGHT);
				}
				for (Sync sync : flight.registry().iterable(spaceObjFilter)) {
					SpaceObj obj = ((ObjectSync<SpaceObj>) sync).value();
					if (!(obj instanceof Asteroid) && obj.isCollision(asteroid)) {
						if (obj instanceof Ship)
							obj.setAlive(false);
						else if (obj instanceof Bullet)
							asteroid.setAlive(false);
					}
				}
			}
		}
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
