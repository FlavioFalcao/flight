package flight.demo.asteroids;

import static flight.demo.asteroids.Server.UPDATES_PER_SECOND;
import static flight.demo.asteroids.Server.UPDATE_DURATION;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_W;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

import flight.net.err.TransmissionException;
import flight.net.syn.ObjectSync;
import flight.net.syn.Sync;
import flight.util.ClassFilter;
import flight.util.Filter;

/**
 * <p>
 * The asteroids client. Maintains a single player's avatar {@link Ship},
 * connects to an asteroids {@link Server}, and provides an graphical interface
 * to interact with the the game. It also wraps a Flight Engine
 * {@link flight.net.Client} which provides network synchronization.
 * </p>
 * <p>
 * Once {@link #run()}, this {@link Client} connect to its assigned asteroids
 * {@link Server} with its member Flight Engine {@link flight.net.Client},
 * synchronize with that {@link Server}'s game environment, and present a
 * graphical user interface for game play.
 * </p>
 * 
 * @author Colby Horn
 * @see Server
 */
public class Client implements Runnable, KeyListener {

	/**
	 * Constructs a new {@link Client}, wrapping the default Flight Engine
	 * {@link flight.net.Client}.
	 */
	public Client() {
		flight = new flight.net.Client();
	}

	/**
	 * Constructs a new {@link Client}, wrapping a Flight Engine
	 * {@link flight.net.Client} set to connect to the specified server.
	 * 
	 * @param serverName
	 *            the server to which the member {@link flight.net.Client} will
	 *            connect
	 */
	public Client(String serverName) {
		flight = new flight.net.Client(serverName);
	}

	/**
	 * Constructs a new {@link Client}, wrapping a Flight Engine
	 * {@link flight.net.Client} set to connect to the specified server on the
	 * specified port.
	 * 
	 * @param serverName
	 *            the server to which the member {@link flight.net.Client} will
	 *            connect
	 * @param serverPort
	 *            the server port to which the member {@link flight.net.Client}
	 *            will connect
	 */
	public Client(String serverName, int serverPort) {
		flight = new flight.net.Client(serverName, serverPort);
	}

	private flight.net.Client	flight;

	/**
	 * The width, in pixels, of the graphic game environment;
	 */
	public static final int		WIDTH	= 800;

	/**
	 * The height, in pixels, of the graphic game environment;
	 */
	public static final int		HEIGHT	= 600;

	/**
	 * Wraps the position of the given {@link SpaceObj} into the bounds of the
	 * game environment
	 * 
	 * @param obj
	 *            the {@link SpaceObj} to be repositioned
	 */
	public static void wrap(SpaceObj obj) {
		if (obj.getX() < 0 || WIDTH < obj.getX()) {
			float mod = obj.getX() % WIDTH;
			obj.setX(0 < mod ? mod : mod + WIDTH);
		}
		if (obj.getY() < 0 || HEIGHT < obj.getY()) {
			float mod = obj.getY() % HEIGHT;
			obj.setY(0 < mod ? mod : mod + HEIGHT);
		}
	}

	private JFrame							window;
	{
		window = new JFrame("Asteroids (Flight Engine Demo)");
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
		window.add(new RenderCanvas());
		window.setSize(WIDTH, HEIGHT);
		window.setResizable(false);
		window.addKeyListener(this);
	}

	/**
	 * A {@link Filter} that selects only {@link ObjectSync}s. Because the only
	 * objects (as opposed to primitives) synchronized the asteroid game are
	 * {@link SpaceObj}s, selecting all {@link ObjectSync}s implicitly selects
	 * only {@link SpaceObj}s.
	 */
	public static final Filter<Sync>		SPACE_OBJ_FILTER	= new ClassFilter<Sync>(
																		new ObjectSync<Object>(
																				new Object()));

	private static final AffineTransform	IDENTITY			= new AffineTransform();

	/**
	 * A canvas for graphically rendering the game environment.
	 */
	@SuppressWarnings("serial")
	private class RenderCanvas extends JPanel {
		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			// clear the screen
			g2.setBackground(Color.BLACK);
			g2.clearRect(0, 0, getWidth(), getHeight());
			// iterate through all space objects
			for (Sync objSync : flight.registry().iterable(SPACE_OBJ_FILTER)) {
				@SuppressWarnings("unchecked")
				SpaceObj obj = ((ObjectSync<SpaceObj>) objSync).value();
				// if each is alive, draw it to the screen
				if (obj.isAlive()) {
					g2.setTransform(IDENTITY);
					g2.translate(obj.getX(), obj.getY());
					g2.rotate(obj.getTheta());
					obj.draw(g2);
				}
			}
		}
	}

	/**
	 * Orders the {@link Client} to connect to its assigned asteroids
	 * {@link Server} and present a graphical user interface for game play.
	 */
	@Override
	public void run() {
		try {
			flight.connect();
			init();
			window.setVisible(true);
			new Timer().schedule(gameLoop, 0, UPDATE_DURATION);
		} catch (IOException | TransmissionException e) {
			System.out.println("error: flight engine server not accessable");
		}
	}

	private TimerTask		gameLoop;
	{
		gameLoop = new TimerTask() {
			@Override
			public void run() {
				update();
				window.repaint();
			}
		};
	}

	private static int		BULLETS	= 10;

	private Ship			ship;
	private List<Bullet>	bullets	= new LinkedList<Bullet>();

	private void init() {
		ship = new Ship();
		ship.register(flight.registry());
		for (int i = 0; i < BULLETS; ++i) {
			Bullet bullet = new Bullet();
			bullet.register(flight.registry());
			bullets.add(bullet);
		}
	}

	private static final float	ACCEL_PER_SECOND	= 10;
	private static final float	ACCEL_UPDATE		= ACCEL_PER_SECOND
															/ UPDATES_PER_SECOND;
	private static final float	TURN_PER_SECOND		= (float) Math.PI;
	private static final float	TURN_UPDATE			= TURN_PER_SECOND
															/ UPDATES_PER_SECOND;
	private Random				random				= new Random();

	/**
	 * Updates this {@link Client}'s avatar ship and associated bullets one time
	 * unit
	 */
	private void update() {
		if (ship.isAlive()) {
			// handle user input when a player's ship is alive
			if (fire) fire();
			if (up) ship.forward(ACCEL_UPDATE);
			if (left) ship.turn(-TURN_UPDATE);
			if (down) ship.forward(-ACCEL_UPDATE);
			if (right) ship.turn(TURN_UPDATE);
			// update the player's ship...
			ship.update();
			// ...and make sure it stays in the game window
			wrap(ship);
		} else {
			// if the player's ship is dead, press fire to respawn
			if (fire)
				ship.spawn(random.nextFloat() * WIDTH, random.nextFloat()
						* HEIGHT, (float) (random.nextFloat() * (2 * Math.PI)));
		}
		// update each bullet, if it is alive
		for (Bullet bullet : bullets)
			if (bullet.isAlive()) bullet.update();
	}

	private void fire() {
		for (Bullet bullet : bullets)
			if (!bullet.isAlive()) {
				bullet.spawn(ship);
				break;
			}
	}

	private boolean	up, left, down, right, fire;
	{
		up = left = down = right = fire = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case VK_UP:
		case VK_W:
			up = true;
			break;
		case VK_LEFT:
		case VK_A:
			left = true;
			break;
		case VK_DOWN:
		case VK_S:
			down = true;
			break;
		case VK_RIGHT:
		case VK_D:
			right = true;
			break;
		case VK_SPACE:
			fire = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case VK_UP:
		case VK_W:
			up = false;
			break;
		case VK_LEFT:
		case VK_A:
			left = false;
			break;
		case VK_DOWN:
		case VK_S:
			down = false;
			break;
		case VK_RIGHT:
		case VK_D:
			right = false;
			break;
		case VK_SPACE:
			fire = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	/**
	 * Starts a single asteroids {@link Client}. Optionally, if a a server name
	 * and port number are specified as the first and second entries,
	 * respectively, in the given command line arguments, they will be passed
	 * along to the asteroids {@link Client}.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		Client asteroids;
		if (args.length >= 2)
			asteroids = new Client(args[0], Integer.parseInt(args[1]));
		else if (args.length >= 1)
			asteroids = new Client(args[0]);
		else
			asteroids = new Client();
		asteroids.run();
	}

}
