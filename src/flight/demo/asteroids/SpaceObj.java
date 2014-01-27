package flight.demo.asteroids;

import java.awt.Graphics2D;
import java.io.Serializable;

import flight.net.syn.BooleanSync;
import flight.net.syn.FloatSync;
import flight.net.syn.ObjectSync;
import flight.net.syn.Sync;
import flight.net.syn.SyncRegistry;

/**
 * <p>
 * An abstract base class for all asteroids game objects. Provides core physics
 * functionality, supplies a interface to paint game objects' graphic
 * representations, and stores the network synchronization objects necessary for
 * multi-player game play.
 * </p>
 * <p>
 * {@link SpaceObj}s can be either dead ( {@link #isAlive()} == false) or alive
 * ( {@link #isAlive()} == true). All {@link SpaceObj}s are initially dead and
 * must {@link #spawn(float, float, float)} at a specified position before they
 * are considered alive. Dead {@link SpaceObj}s are typically not graphically
 * rendered and cannot {@link #collide(SpaceObj)} with any other
 * {@link SpaceObj}s.
 * </p>
 * <p>
 * {@link SpaceObj}s and their member sync fields are not initially registered
 * for synchronization the Flight Engine. In order to begin synchronization, the
 * method {@link #register(SyncRegistry)} must be called and passed a valid
 * Flight Engine {@link SyncRegistry}.
 * </p>
 * 
 * @author Colby Horn
 */
@SuppressWarnings("serial")
public abstract class SpaceObj implements Serializable {

	/**
	 * Constructs a {@link SpaceObj} with the specified size.
	 * 
	 * @param size
	 *            the radius of a circle approximating the {@link SpaceObj}'s
	 *            physical representation
	 */
	public SpaceObj(float size) {
		this.size = size;
	}

	/**
	 * <p>
	 * Do not modify directly! Access only indirectly through {@link SpaceObj}
	 * methods!
	 * </p>
	 * <p>
	 * The Flight Engine {@link Sync} containing the network synchronized alive
	 * variable of this {@link SpaceObj}. Must be public to allow the flight
	 * engine access during network deserialization.
	 * </p>
	 */
	public BooleanSync	alive	= new BooleanSync(false);

	/**
	 * Returns whether this {@link SpaceObj} is alive.
	 * 
	 * @return whether this {@link SpaceObj} is alive
	 */
	public boolean isAlive() {
		return alive.value();
	}

	protected void setAlive(boolean alive) {
		this.alive.value(alive);
	}

	/**
	 * Sets this {@link SpaceObj} to be dead.
	 */
	public void die() {
		setAlive(false);
	}

	/**
	 * Spawns this {@link SpaceObj} in the specified location and sets it to be
	 * alive.
	 * 
	 * @param x
	 *            the new x-coordinate at which to spawn
	 * @param y
	 *            the new y-coordinate at which to spawn
	 * @param theta
	 *            the new rotation with which to spawn
	 */
	public void spawn(float x, float y, float theta) {
		setAlive(true);
		setX(x);
		setY(y);
		setTheta(theta);
		dx = dy = 0;
	}

	/**
	 * <p>
	 * Do not modify directly! Access only indirectly through {@link SpaceObj}
	 * methods!
	 * </p>
	 * <p>
	 * The Flight Engine {@link Sync} containing the network synchronized
	 * x-coordinate of this {@link SpaceObj}. Must be public to allow the flight
	 * engine access during network deserialization.
	 * </p>
	 */
	public FloatSync	x	= new FloatSync(0);

	/**
	 * <p>
	 * Do not modify directly! Access only indirectly through {@link SpaceObj}
	 * methods!
	 * </p>
	 * <p>
	 * The Flight Engine {@link Sync} containing the network synchronized
	 * y-coordinate of this {@link SpaceObj}. Must be public to allow the flight
	 * engine access during network deserialization.
	 * </p>
	 */
	public FloatSync	y	= new FloatSync(0);

	/**
	 * Returns the x-coordinate of this {@link SpaceObj}.
	 * 
	 * @return the x-coordinate of this {@link SpaceObj}
	 */
	public float getX() {
		return x.value();
	}

	protected void setX(float x) {
		this.x.value(x);
	}

	/**
	 * Returns the y-coordinate of this {@link SpaceObj}.
	 * 
	 * @return the y-coordinate of this {@link SpaceObj}
	 */
	public float getY() {
		return y.value();
	}

	protected void setY(float y) {
		this.y.value(y);
	}

	protected float	dx, dy;

	/**
	 * Returns the horizontal velocity this {@link SpaceObj}.
	 * 
	 * @return the horizontal velocity this {@link SpaceObj}
	 */
	public float getDX() {
		return dx;
	}

	protected void incDX(float d2x) {
		dx += d2x;
	}

	/**
	 * Returns the vertical velocity this {@link SpaceObj}.
	 * 
	 * @return the vertical velocity this {@link SpaceObj}
	 */
	public float getDY() {
		return dy;
	}

	protected void incDY(float d2y) {
		dy += d2y;
	}

	/**
	 * Accelerates this {@link SpaceObj} in the direction of it's current
	 * heading by the specified acceleration.
	 * 
	 * @param accel
	 *            the amount by which to accelerate
	 */
	public void forward(float accel) {
		incDX((float) (Math.cos(getTheta()) * accel));
		incDY((float) (Math.sin(getTheta()) * accel));
	}

	/**
	 * Updates the position of this {@link SpaceObj} according to it's velocity
	 * over one unit of time. Should be called on a regular basis to simulate
	 * smooth motion.
	 */
	public void update() {
		setX(getX() + getDX());
		setY(getY() + getDY());
	}

	/**
	 * <p>
	 * Do not modify directly! Access only indirectly through {@link SpaceObj}
	 * methods!
	 * </p>
	 * <p>
	 * The Flight Engine {@link Sync} containing the network synchronized
	 * rotation of this {@link SpaceObj}. Must be public to allow the flight
	 * engine access during network deserialization.
	 * </p>
	 */
	public FloatSync	theta	= new FloatSync(0);

	/**
	 * Returns the theta or rotation of this {@link SpaceObj}, in radians.
	 * 
	 * @return the theta or rotation of this {@link SpaceObj}, in radians
	 */
	public float getTheta() {
		return theta.value();
	}

	protected void setTheta(float theta) {
		this.theta.value(theta);
	}

	/**
	 * Rotates this {@link SpaceObj} by the specified angle.
	 * 
	 * @param dtheta
	 *            an angle, in radians ; can be either positive of negative
	 */
	public void turn(float dtheta) {
		theta.value(theta.value() + dtheta);
	}

	private float	size;

	/**
	 * Returns the size or radius of a circle approximating the physical
	 * representation of this {@link SpaceObj}.
	 * 
	 * @return the size or radius of a circle approximating the physical
	 *         representation of this {@link SpaceObj}
	 */
	public float getSize() {
		return size;
	}

	/**
	 * Returns whether a collision has occurred between this {@link SpaceObj}
	 * and the specified one. A collision is defined simply to be when the
	 * distance between two {@link SpaceObj}s' positions is less than the sum of
	 * their sizes.
	 * 
	 * @param obj
	 * @return whether a collision has occurred between this {@link SpaceObj}
	 *         and the given one
	 */
	public boolean collide(SpaceObj obj) {
		return isAlive()
				&& obj.isAlive()
				&& Math.sqrt(Math.pow(getX() - obj.getX(), 2)
						+ Math.pow(getY() - obj.getY(), 2)) < getSize()
						+ obj.getSize();
	}

	/**
	 * Draws a graphic representation of this {@link SpaceObj} through the
	 * specified {@link Graphics2D} object at its current drawing location.
	 * 
	 * @param g2
	 *            a {@link Graphics2D} object to which a graphic representation
	 *            of this {@link SpaceObj} will be drawn
	 */
	public abstract void draw(Graphics2D g2);

	@Override
	public String toString() {
		return getClass().getSimpleName() + " pos=(" + getX() + ", " + getY()
				+ ") vel=(" + getDX() + ", " + getDY() + ")";
	}

	/**
	 * Registers the {@link SpaceObj} and its member sync fields with the
	 * specified {@link SyncRegistry}. This will cause this {@link SpaceObj} and
	 * its fields to be synchronized across the network through all the
	 * connected Flight Engines. This method should be called at most once for
	 * any {@link SpaceObj}.
	 * 
	 * @param registry
	 *            the {@link SyncRegistry} with which this {@link SpaceObj} will
	 *            be registered
	 */
	public void register(SyncRegistry registry) {
		registry.register(alive);
		registry.register(x);
		registry.register(y);
		registry.register(theta);
		registry.register(new ObjectSync<SpaceObj>(this));
	}
}
