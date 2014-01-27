package flight.demo.asteroids;

import static flight.demo.asteroids.Client.HEIGHT;
import static flight.demo.asteroids.Client.WIDTH;
import static flight.demo.asteroids.Client.SPACE_OBJ_FILTER;
import static flight.demo.asteroids.Client.wrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import flight.net.syn.ObjectSync;
import flight.net.syn.Sync;

/**
 * <p>
 * The asteroids server. Maintains and updates the game environment (i.e. all
 * {@link Asteroid}s) including managing collision detection. It also wraps the
 * Flight Engine {@link flight.net.Server} which provides network
 * synchronization.
 * </p>
 * <p>
 * Once {@link #run()}, this {@link Server} will begin simulating the game
 * environment and accepting {@link flight.net.Client} connections with its
 * member Flight Engine {@link flight.net.Server}.
 * </p>
 * 
 * @author Colby Horn
 * @see Client
 */
public class Server implements Runnable {

	/**
	 * Constructs a new {@link Server}, wrapping the default Flight Engine
	 * {@link flight.net.Server}.
	 */
	public Server() {
		flight = new flight.net.Server();
	}

	/**
	 * Constructs a new {@link Server}, wrapping a Flight Engine
	 * {@link flight.net.Server} on the specified port.
	 * 
	 * @param serverPort
	 *            a valid port on which {@link flight.net.Client} connections
	 *            will be accepted
	 */
	public Server(int serverPort) {
		flight = new flight.net.Server(serverPort);
	}

	flight.net.Server	flight;

	/**
	 * The number of times the game will attempt to update per second.
	 */
	public static final long	UPDATES_PER_SECOND	= 20;

	/**
	 * The amount of time, in milliseconds, each game update is expected to
	 * take.
	 */
	public static final long	UPDATE_DURATION		= 1000 / UPDATES_PER_SECOND;

	/**
	 * Orders this {@link Server} begin simulating the game environment and open
	 * its member Flight Engine {@link flight.net.Server} to accept
	 * {@link flight.net.Client} connections.
	 */
	@Override
	public void run() {
		try {
			flight.start();
			init();
			new Timer().schedule(gameLoop, 0, UPDATE_DURATION);
		} catch (IOException e) {
			System.out
					.println("error: flight engine server could not be started");
		}
	}

	private TimerTask			gameLoop;
	{
		gameLoop = new TimerTask() {
			@Override
			public void run() {
				update();
			}
		};
	}

	private static final int	ASTEROIDS				= 20;
	private static final float	MIN_SIZE				= 10;
	private static final float	MAX_SIZE				= 40;
	private static final float	MAX_ROTATION			= (float) (Math.PI / 64);
	private static final float	MAX_SPEED_PER_SECOND	= 25;
	private static final float	MAX_SPEED_PER_UPDATE	= MAX_SPEED_PER_SECOND
																/ UPDATES_PER_SECOND;

	private Random				random					= new Random();
	private List<Asteroid>		asteroids				= new ArrayList<Asteroid>();

	/**
	 * Initializes the game environment by constructing a batch of randomly
	 * generated {@link Asteroids}.
	 */
	private void init() {
		for (int i = 0; i < ASTEROIDS; ++i) {
			// construct a new asteroid
			Asteroid asteroid = new Asteroid(random.nextFloat()
					* (MAX_SIZE - MIN_SIZE) + MIN_SIZE, random.nextFloat()
					* (2 * MAX_ROTATION) - MAX_ROTATION);
			asteroids.add(asteroid);
			// spawn it in a random location
			asteroid.spawn(random.nextFloat() * WIDTH, random.nextFloat()
					* HEIGHT, (float) (random.nextFloat() * (2 * Math.PI)));
			// give it a random velocity
			asteroid.forward(random.nextFloat() * MAX_SPEED_PER_UPDATE);
			// and, finally, register the asteroid for network synchronization
			asteroid.register(flight.registry());
		}
	}

	/**
	 * Updates the game environment a single time unit and checks for
	 * {@link SpaceObj} collisions.
	 */
	private void update() {
		for (Asteroid asteroid : asteroids) {
			if (asteroid.isAlive()) {
				// update each 'living' asteroid...
				asteroid.update();
				// ...and make sure it stays in the game window
				wrap(asteroid);
				// iterate through all game objects...
				for (Sync sync : flight.registry().iterable(SPACE_OBJ_FILTER)) {
					@SuppressWarnings("unchecked")
					SpaceObj obj = ((ObjectSync<SpaceObj>) sync).value();
					// ...selecting only ships and bullets (owned by clients)
					if (!(obj instanceof Asteroid) && obj.collide(asteroid)) {
						// if a a ship strikes this asteroid, destroy the ship
						if (obj instanceof Ship)
							obj.die();
						// if a bullet strikes this asteroid, instead destroy
						// the asteroid
						else if (obj instanceof Bullet)
							asteroid.die();
					}
				}
			}
		}
	}

	/**
	 * Starts a single asteroids {@link Server}. Optionally, if a port number is
	 * specified as the first entry in the given command line arguments, it will
	 * be passed along to the asteroids {@link Server}.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		Server asteroidsServer;
		if (args.length >= 1)
			asteroidsServer = new Server(Integer.parseInt(args[0]));
		else
			asteroidsServer = new Server();
		asteroidsServer.run();
	}

}
