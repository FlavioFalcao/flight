package flight.demo.asteroids;

import java.awt.Graphics2D;
import java.io.Serializable;

import flight.net.syn.BooleanSync;
import flight.net.syn.FloatSync;
import flight.net.syn.ObjectSync;
import flight.net.syn.SyncRegistry;

@SuppressWarnings("serial")
public abstract class SpaceObj implements Serializable {

	public SpaceObj(float size) {
		this.size = size;
	}

	public BooleanSync	alive	= new BooleanSync(false);

	public boolean isAlive() {
		return alive.value();
	}

	public void setAlive(boolean alive) {
		this.alive.value(alive);
	}

	public FloatSync	x	= new FloatSync(0), y = new FloatSync(0);

	public float getX() {
		return x.value();
	}

	public void setX(float x) {
		this.x.value(x);
	}

	public float getY() {
		return y.value();
	}

	public void setY(float y) {
		this.y.value(y);
	}

	public FloatSync	theta	= new FloatSync(0);

	public float getTheta() {
		return theta.value();
	}

	private void setTheta(float theta) {
		this.theta.value(theta);
	}

	public void turn(float dtheta) {
		theta.value(theta.value() + dtheta);
	}

	public void spawn(float x, float y, float theta) {
		setAlive(true);
		setX(x);
		setY(y);
		setTheta(theta);
		dx = dy = 0;
	}

	protected float	dx, dy;

	public float getDX() {
		return dx;
	}

	private void incDX(float d2x) {
		dx += d2x;
	}

	public float getDY() {
		return dy;
	}

	private void incDY(float d2y) {
		dy += d2y;
	}

	public void forward(float accel) {
		incDX((float) (Math.cos(getTheta()) * accel));
		incDY((float) (Math.sin(getTheta()) * accel));
	}

	public void update() {
		setX(getX() + getDX());
		setY(getY() + getDY());
	}

	private float	size;

	public float getSize() {
		return size;
	}

	public boolean isCollision(SpaceObj obj) {
		return isAlive()
				&& obj.isAlive()
				&& Math.sqrt(Math.pow(getX() - obj.getX(), 2)
						+ Math.pow(getY() - obj.getY(), 2)) < getSize()
						+ obj.getSize();
	}

	public abstract void draw(Graphics2D g2);

	@Override
	public String toString() {
		return getClass().getSimpleName() + " pos=(" + getX() + ", " + getY()
				+ ") vel=(" + getDX() + ", " + getDY() + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpaceObj) {
			SpaceObj spaceObj = (SpaceObj) obj;
			return syncId == spaceObj.syncId;
		} else
			return false;
	}

	private int	syncId	= -1;

	public void register(SyncRegistry registry) {
		if (syncId == -1) {
			registry.register(alive);
			registry.register(x);
			registry.register(y);
			registry.register(theta);
			ObjectSync<SpaceObj> objSync = new ObjectSync<SpaceObj>(this);
			registry.register(objSync);
			syncId = objSync.getId();
		}
	}
}
