package flight.demo.asteroids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;

@SuppressWarnings("serial")
public class Ship extends SpaceObj {

	public Ship() {
		super(10);
	}
	
	private static Shape	shape;
	{
		int[] xs = { 10, -10, -10 }, ys = { 0, 10, -10 };
		shape = new Polygon(xs, ys, xs.length);
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(Color.RED);
		g2.draw(shape);
	}
}
