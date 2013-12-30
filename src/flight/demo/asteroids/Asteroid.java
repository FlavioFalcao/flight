package flight.demo.asteroids;

import java.awt.Color;
import java.awt.Graphics2D;

@SuppressWarnings("serial")
public class Asteroid extends SpaceObj {

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
