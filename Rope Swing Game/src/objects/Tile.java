package objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Collision;

public class Tile {
	public int x, y, w, h;
	
	public Tile(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public void draw(double px, double py, int pw, int ph, Graphics2D graphics) {
		if(Collision.checkCollision(x, y, w, h, px, py, pw, ph)) {graphics.setColor(Color.BLUE);}
		else {graphics.setColor(Color.RED);}
		
		graphics.fillRect(x, y, w, h);
	}
}
