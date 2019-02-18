package objects;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import main.Collision;
import main.Physics;

public class Player {
	public final double X_ACCEL = 0.1;
	public final double Y_ACCEL = 0.1;
	public final double X_MAX = 5;
	public final double Y_MAX = 5;
	
	public double x, y, dx, dy, da; // dx = delta x (x speed), dy = delta y (y speed), da = delta angular (angular speed)
	
	public int w = 32;
	public int h = 32;
	
	Color color = Color.WHITE;
	
	boolean up, down, left, right, space;
	
	double distance = 100;
	double[] anchor;
	double angle;
	boolean swinging = false;
	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		
		anchor = new double[]{320 + 100 - w / 2, 240 - h / 2};
		//angle = Math.acos((x - anchor[0]) / distance);
	}
	
	public void update(int[/* index */][/* x, y, w, h */] tiles) {
		double[] pop;
		
		if(left) {dx -= X_ACCEL;}
		if(right) {dx += X_ACCEL;}
		if(up) {dy -= Y_ACCEL;}
		if(down) {dy += Y_ACCEL;}
		if(space) {
			if(!swinging) {
				swinging = true;
				distance = Math.abs(Math.sqrt(((x - anchor[0]) * (x - anchor[0])) + ((y - anchor[1]) * (y - anchor[1]))));
				angle = Math.acos((x - anchor[0]) / distance);
				if((y - anchor[1]) < 0) {angle = -angle;}
				
				da = Physics.linearDelta2AngularDelta(angle, dx, dy);
			}
		}
		else {
			if(swinging) {
				swinging = false;
				dx = 0;
				dy = 0;
			}
		}
		
		if(dx < -X_MAX) {dx = -X_MAX;}
		if(dx > X_MAX) {dx = X_MAX;}
		if(dy < -Y_MAX) {dy = -Y_MAX;}
		if(dy > Y_MAX) {dy = Y_MAX;}
		
		//angle = Math.acos((x - anchor[0]) / distance);
		if(swinging) {
			angle -= da / distance;
			//angle -= dy / distance;
			
			if(angle > Math.PI * 2) {angle -= Math.PI * 2;}
			if(angle < 0) {angle += Math.PI * 2;}
			
			x = Math.cos(angle) * distance + anchor[0];
			y = Math.sin(angle) * distance + anchor[1];
			
		}
		
		if(!swinging) {x += dx;}
		pop = Collision.popOut(x, y, (double)w, (double)h, tiles);
		x += pop[0];
		y += pop[1];
		if(!swinging) {
			if(pop[0] != 0) {dx = 0;}
			if(pop[1] != 0) {dy = 0;}
		}
		else {
			if(pop[0] != 0 || pop[1] != 0) {
				dx = 0;
				dy = 0;
			}
		}
		
		if(!swinging) {y += dy;}
		pop = Collision.popOut(x, y, (double)w, (double)h, tiles);
		x += pop[0];
		y += pop[1];
		if(!swinging) {
			if(pop[0] != 0) {dx = 0;}
			if(pop[1] != 0) {dy = 0;}
		}
		else {
			if(pop[0] != 0 || pop[1] != 0) {
				dx = 0;
				dy = 0;
			}
		}
		
		boolean collided = false;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(x, y, w, h, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {collided = true;}}
		
		if(collided) {color = Color.GREEN;}
		else {color = Color.WHITE;}
	}
	
	public void draw(Graphics2D graphics) {
		graphics.setColor(color);
		graphics.fillRect((int)x, (int)y, w, h);
		
		graphics.drawLine((int)x + w / 2, (int)y + h / 2, (int)anchor[0] + w / 2, (int)anchor[1] + h / 2);
	}
	
	public void keyPressed(int key) {
		switch(key) {
			case VK_UP: up = true; break;
			case VK_DOWN: down = true; break;
			case VK_LEFT: left = true; break;
			case VK_RIGHT: right = true; break;
			case VK_SPACE: space = true; break;
			//case VK_SHIFT: shift = true; break;
		}
	}
	public void keyReleased(int key) {
		switch(key) {
			case VK_UP: up = false; break;
			case VK_DOWN: down = false; break;
			case VK_LEFT: left = false; break;
			case VK_RIGHT: right = false; break;
			case VK_SPACE: space = false; break;
			//case VK_SHIFT: shift = false; break;
		}
	}

	public void mouseClicked(MouseEvent mouse) {}
	public void mouseEntered(MouseEvent mouse) {}
	public void mouseExited(MouseEvent mouse) {}
	public void mousePressed(MouseEvent mouse) {}
	public void mouseReleased(MouseEvent mouse) {}
}
