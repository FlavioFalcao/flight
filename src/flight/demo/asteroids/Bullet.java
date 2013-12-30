package flight.demo.asteroids;

import static flight.demo.asteroids.Client.UPDATES_PER_SECOND;

import java.awt.Color;
import java.awt.Graphics2D;

@SuppressWarnings("serial")
public class Bullet extends SpaceObj {

	public Bullet() {
		super(1);
	}

	private static float	SPEED	= 50 / UPDATES_PER_SECOND;

	public void spawn(float x, float y, float theta, float dx, float dy) {
		super.spawn(x, y, theta);
		this.dx = dx;
		this.dy = dy;
		forward(SPEED);
		duration = 0;
	}

	private static final int	MAX_DURATION	= (int) (2 * UPDATES_PER_SECOND);

	private int					duration;

	@Override
	public void update() {
		super.update();
		if (++duration > MAX_DURATION)
			setAlive(false);
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(Color.YELLOW);
		g2.drawRect(0, 0, 1, 1);
	}

}
