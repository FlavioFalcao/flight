package flight.demo.asteroids;

import java.awt.Color;
import java.awt.Graphics2D;

@SuppressWarnings("serial")
public class Asteroid extends SpaceObj {

	protected Asteroid(float x, float y, float theta, float size, float dtheta) {
		super(x, y, theta);
		this.size = size;
		this.dtheta = dtheta;
	}

	private float	size;
	private float	dtheta;

	public void update() {
		super.update();
		turn(dtheta);
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(Color.GRAY);
		g2.drawRect((int) (-size / 2), (int) (-size / 2), (int) size,
				(int) size);
	}

}
