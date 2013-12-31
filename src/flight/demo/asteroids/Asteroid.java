package flight.demo.asteroids;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * <p>
 * A class representing the game's titular object. Inherits game play mechanics,
 * physics, and network synchronization from it's {@link SpaceObj} superclass.
 * </p>
 * <p>
 * Adds a constant rotation to {@link SpaceObj} physics in order to simulate
 * spinning asteroids in zero gravity.
 * </p>
 * 
 * @author Colby Horn
 */
@SuppressWarnings("serial")
public class Asteroid extends SpaceObj {

	/**
	 * Constructs a new {@link Asteroid} with the specified size and rotation
	 * speed
	 * 
	 * @param size
	 *            the size of the new {@link Asteroid} as defined in
	 *            {@link SpaceObj#SpaceObj(float)}
	 * @param dtheta
	 *            the rotation speed of the new {@link Asteroid}
	 */
	public Asteroid(float size, float dtheta) {
		super(size);
		this.dtheta = dtheta;
	}
	
	private float	dtheta;

	@Override
	public void update() {
		super.update();
		turn(dtheta);
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(Color.GRAY);
		g2.drawRect((int) -getSize(), (int) -getSize(), (int) getSize() * 2,
				(int) getSize() * 2);
	}

}
