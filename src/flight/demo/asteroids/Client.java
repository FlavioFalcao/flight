package flight.demo.asteroids;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_S;
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
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import flight.net.err.TransmissionException;
import flight.net.syn.ObjectSync;
import flight.net.syn.Sync;
import flight.util.Filter;

public class Client implements Runnable, KeyListener {

	public Client() {
		flight = new flight.net.Client();
	}

	public Client(String serverName) {
		flight = new flight.net.Client(serverName);
	}

	public Client(String serverName, int serverPort) {
		flight = new flight.net.Client(serverName, serverPort);
	}

	private flight.net.Client				flight;

	public static final int					WIDTH		= 800;
	public static final int					HEIGHT		= 600;

	private JFrame							window;
	{
		window = new JFrame("Asteroids (Flight Engine Demo)");
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
		window.add(new RenderCanvas());
		window.setSize(WIDTH, HEIGHT);
		window.setResizable(false);
		window.addKeyListener(this);
	}

	private static final AffineTransform	identity	= new AffineTransform();

	private static Filter<Sync>				spaceObjFilter;
	{
		spaceObjFilter = new Filter<Sync>() {
			@Override
			public boolean select(Sync element) {
				return element instanceof ObjectSync<?>;
			}
		};
	}

	@SuppressWarnings("serial")
	private class RenderCanvas extends JPanel {
		@SuppressWarnings("unchecked")
		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setBackground(Color.BLACK);
			g2.clearRect(0, 0, getWidth(), getHeight());
			for (Sync objSync : flight.registry().iterable(spaceObjFilter)) {
				SpaceObj obj = ((ObjectSync<SpaceObj>) objSync).value();
				g2.setTransform(identity);
				g2.translate(obj.getX(), obj.getY());
				g2.rotate(obj.getTheta());
				obj.draw(g2);
			}
		}
	}

	private static long	UPDATES_PER_SECOND	= 20;
	private static long	UPDATE_DURATION		= 1000 / UPDATES_PER_SECOND;

	@Override
	public void run() {
		try {
			flight.connect();
			ship = new Ship(random.nextFloat() * WIDTH, random.nextFloat()
					* HEIGHT, (float) (random.nextFloat() * (2 * Math.PI)));
			ship.register(flight.registry());
			window.setVisible(true);
			while (true) {
				update();
				window.repaint();
				Thread.sleep(UPDATE_DURATION);
			}
		} catch (IOException | TransmissionException e) {
			System.out.println("error: flight engine server not accessable");
		} catch (InterruptedException e) {}
	}

	private static float	ACCEL_PER_SECOND	= 10;
	private static float	ACCEL_UPDATE		= ACCEL_PER_SECOND
														/ UPDATES_PER_SECOND;
	private static float	TURN_PER_SECOND		= (float) (Math.PI / 2);
	private static float	TURN_UPDATE			= TURN_PER_SECOND
														/ UPDATES_PER_SECOND;

	private Ship			ship;
	private Random			random				= new Random();

	private void update() {
		if (up)
			ship.forward(ACCEL_UPDATE);
		if (left)
			ship.turn(-TURN_UPDATE);
		if (down)
			ship.forward(-ACCEL_UPDATE);
		if (right)
			ship.turn(TURN_UPDATE);
		ship.update();
		if (ship.getX() < 0 || WIDTH < ship.getX()) {
			float mod = ship.getX() % WIDTH;
			ship.setX(0 < mod ? mod : mod + WIDTH);
		}
		if (ship.getY() < 0 || HEIGHT < ship.getY()) {
			float mod = ship.getY() % HEIGHT;
			ship.setY(0 < mod ? mod : mod + HEIGHT);
		}
	}

	private boolean	up, left, down, right;
	{
		up = left = down = right = false;
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
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

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
