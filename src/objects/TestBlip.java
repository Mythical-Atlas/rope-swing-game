package objects;

import java.awt.Color;
import java.awt.Graphics2D;

public class TestBlip {
	double x, y;
	
	int counter = 0;
	
	public boolean exists = true;
	
	public TestBlip(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void update() {
		counter++;
		if(counter == 60) {exists = false;}
	}
	
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.RED);
		graphics.fillOval((int)x - 5, (int)y - 5, 10, 10);
	}
}
