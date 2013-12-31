package flight.demo.asteroids;

import static flight.demo.asteroids.Server.UPDATES_PER_SECOND;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * <p>
 * A class representing a {@link Ship}s bullet. Inherits game play mechanics,
 * physics, and network synchronization from it's {@link SpaceObj} superclass.
 * </p>
 * <p>
 * Adds a life duration to {@link SpaceObj}'s lifespan mechanics which
 * automatically kills each {@link Bullet} after a certain time has elapsed from
 * its spawn, indirectly defining a firing range.
 * </p>
 * 
 * @author Colby Horn
 */
@SuppressWarnings("serial")
public class Bullet extends SpaceObj {

	/**
	 * Constructs a new {@link Bullet}
	 */
	public Bullet() {
		super(1);
	}

	private static final float	MOVE_PER_SECOND	= 50;
	private static final float	MOVE_PER_UPDATE	= MOVE_PER_SECOND
														/ UPDATES_PER_SECOND;

	/**
	 * Spawns this {@link Bullet} as if it had been fired from the specified
	 * {@link Ship}, setting its position, rotation, and velocity accordingly.
	 * 
	 * @param ship
	 *            the ship from which this bullet it to be fired
	 */
	public void spawn(Ship ship) {
		super.spawn(ship.getX(), ship.getY(), ship.getTheta());
		this.dx = ship.getDX();
		this.dy = ship.getDY();
		forward(MOVE_PER_UPDATE);
		duration = 0;
	}

	private static final int	DURATION_IN_SECONDS	= 2;
	private static final int	DURATION_IN_UPDATES	= (int) (DURATION_IN_SECONDS * UPDATES_PER_SECOND);

	private int					duration;

	@Override
	public void update() {
		super.update();
		if (++duration > DURATION_IN_UPDATES)
			die();
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(Color.YELLOW);
		g2.drawRect(0, 0, 1, 1);
	}

}
