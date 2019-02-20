package objects;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import main.Collision;
import main.Physics;

public class Player {
	public final double X_ACCEL = 0.1;
	public final double A_ACCEL = 0.1;
	
	public final double X_DECEL = 0.2;
	public final double A_DECEL = 0.01;
	
	public final double X_MAX = 10;
	//public final double Y_MAX = 10;
	public final double A_MAX = 10;
	
	public final double JUMP_FORCE = 5;
	public final double JUMP_DECEL = 1;
	
	public final double LINEAR_GRAVITY = 0.2;
	public final double ANGULAR_GRAVITY = 0.2;
	
	public double x, y, dx, dy, da; // dx = delta x (x speed), dy = delta y (y speed), da = delta angular (angular speed)
	
	public int w = 16;
	public int h = 16;
	
	Color color = Color.WHITE;
	
	boolean up, down, left, right, space;
	
	boolean ground;
	
	boolean jumping;
	boolean canJump;
	
	double distance;
	double maxDistance;
	double[] anchor;
	double angle;
	boolean canSwing;
	boolean swinging = false;
	
	boolean mouseInWindow = false;
	double xMouse;
	double yMouse;
	double xMouseRay;
	double yMouseRay;
	boolean canHook;
	
	boolean reset;
	
	double ox, oy;
	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		
		ox = x;
		oy = y;
	}
	
	public void update(int[/* index */][/* x, y, w, h */] tiles, JFrame frame) {
		if(mouseInWindow) {
			Point mousePos = MouseInfo.getPointerInfo().getLocation(); // gets mouse pos on monitor
			
			// translate above position to be mouse position in frame of window
			xMouse = mousePos.x - frame.getLocationOnScreen().x - 3;
			yMouse = mousePos.y - frame.getLocationOnScreen().y - 26;
			
			// default value for mouse line
			xMouseRay = xMouse;
			yMouseRay = yMouse;
			
			// mouse line = line between center of player and mouse
			
			canHook = false;
			
			// check if mouse line is colliding with any tiles
			int stopped = 0;
			for(int i = 0; i < tiles.length; i++) {if(Collision.lineRect(x + w / 2, y + h / 2, xMouse, yMouse, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {stopped++;}}
			
			// if so, trim the line so that it goes from the center of the player to the closest tile on the line
			if(stopped > 0) {
				int[][] collidingTiles = new int[stopped][4];
				
				canHook = true;
				
				// get list of tiles colliding with mouse line
				int index = 0;
				for(int i = 0; i < tiles.length; i++) {
					if(Collision.lineRectEdges(x + w / 2, y + h / 2, xMouse, yMouse, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {
						collidingTiles[index] = tiles[i];
						index++;
					}
				}
				
				// check which sides of the colliding tiles pass through mouse line
				int numPossibilities = stopped * 4;
				double[][] possibilities = new double[numPossibilities][2];
				boolean[] works = new boolean[numPossibilities];
				
				for(int i = 0; i < stopped; i++) {
					boolean[] tempBools = Collision.whichLineRectEdges(x + w / 2, y + h / 2, xMouse, yMouse, collidingTiles[i][0], collidingTiles[i][1], collidingTiles[i][2], collidingTiles[i][3]);
				
					works[i * 4 + 0] = tempBools[0];
					works[i * 4 + 1] = tempBools[1];
					works[i * 4 + 2] = tempBools[2];
					works[i * 4 + 3] = tempBools[3];
				}
				
				// gets intersecting point for each side that collides with the mouse line
				for(int o = 0; o < numPossibilities; o++) {
					if(works[o]) {
						double x1 = 0;
						double y1 = 0;
						double x2 = 0;
						double y2 = 0;
						
						int i = (int)Math.floor(o / 4);
						
						// check which edge is currently being checked
						if(o % 4 == 0) { // left
							x1 = collidingTiles[i][0];
							y1 = collidingTiles[i][1];
							x2 = collidingTiles[i][0];
							y2 = collidingTiles[i][1] + collidingTiles[i][3];
						}
						if(o % 4 == 1) { // right
							x1 = collidingTiles[i][0] + collidingTiles[i][2];
							y1 = collidingTiles[i][1];
							x2 = collidingTiles[i][0] + collidingTiles[i][2];
							y2 = collidingTiles[i][1] + collidingTiles[i][3];
						}
						if(o % 4 == 2) { // top
							x1 = collidingTiles[i][0];
							y1 = collidingTiles[i][1];
							x2 = collidingTiles[i][0] + collidingTiles[i][2];
							y2 = collidingTiles[i][1];
						}
						if(o % 4 == 3) { // bottom
							x1 = collidingTiles[i][0];
							y1 = collidingTiles[i][1] + collidingTiles[i][3];
							x2 = collidingTiles[i][0] + collidingTiles[i][2];
							y2 = collidingTiles[i][1] + collidingTiles[i][3];
						}
						
						possibilities[o] = Collision.whereLineLine(x + w / 2, y + h / 2, xMouse, yMouse, x1, y1, x2, y2);}
				}
				
				// find intersecting point closest to player
				// intersecting point refering to point of intersection between mouse line and a given side of a tile
				double smallestDistance = 0;
				int smallestIndex = -1;
				for(int i = 0; i < possibilities.length; i++) {
					if(works[i]) {
						double xDist = Math.abs((x + w / 2) - possibilities[i][0]);
						double yDist = Math.abs((y + h / 2) - possibilities[i][1]);
						double tempDistance = Math.sqrt(xDist * xDist + yDist * yDist);
						
						if(tempDistance < smallestDistance || smallestIndex == -1) {
							smallestDistance = tempDistance;
							smallestIndex = i;
						}
					}
				}
				
				// set mouseRay position to be closest intersecting point
				if(smallestIndex != -1) {
					xMouseRay = possibilities[smallestIndex][0];
					yMouseRay = possibilities[smallestIndex][1];
				}
			}
		}
		
		if(reset) { // reset player (useful if debugging and no stage bounds)
			reset = false;
			
			x = ox;
			y = oy;
			dx = 0;
			dy = 0;
			da = 0;
			canSwing = false;
			swinging = false;
			canHook = false;
		}
		
		// set ground flag if row of pixels below player is colliding with tile
		ground = false;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(x, y + h, w, 1, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {ground = true;}}
		
		// move player on ground
		if(left && !right && dx > -X_MAX) {
			dx -= X_ACCEL;
			if(dx > 0 && ground) {dx -= X_DECEL;} // additional speed if going against current velocity
		}
		if(right && !left && dx < X_MAX) {
			dx += X_ACCEL;
			if(dx < 0 && ground) {dx += X_DECEL;} // additional speed if going against current velocity
		}
		
		if(ground) {
			// apply friction
			if(!left && !right || left && right) {
				if(dx < -X_ACCEL) {dx += X_DECEL;}
				else if(dx > X_ACCEL) {dx -= X_DECEL;}
				else {dx = 0;}
			}
			
			// trim rope while on ground
			if(canSwing) {maxDistance = Math.abs(Math.sqrt(((x + w / 2 - anchor[0]) * (x + w / 2 - anchor[0])) + ((y + h / 2 - anchor[1]) * (y + h / 2 - anchor[1]))));}
			
			// jump
			if(space && canJump) {
				canJump = false;
				jumping = true;
				ground = false;
				
				dy -= JUMP_FORCE;
			}
			if(!space) {canJump = true;} // allow to jump with space, but only if on ground and space is released
		}
		else {
			// step jump
			// holding space after jumping allows for extra height (more like allows default height - releasing space decelerates player quickly)
			if(!swinging) {
				if(jumping) {
					if(!space) {dy += JUMP_DECEL;}

					if(dy >= 0) {jumping = false;}
				}
			}
			else {jumping = false;}
		}
		
		// move player while swinging
		if(swinging) {
			if(left && da > -A_MAX) {
				/*if((y + h / 2 - anchor[1]) < 0) {da += A_ACCEL;}
				else {*/da -= A_ACCEL;//}
			}
			if(right && da < A_MAX) {
				/*if((y + h / 2 - anchor[1]) < 0) {da -= A_ACCEL;}
				else {*/da += A_ACCEL;//}
			}
		}
		
		// check if swinging state should be changed
		if(canSwing) {
			if(!swinging) { // not currently swinging but should be
				distance = Math.abs(Math.sqrt(((x + dx + w / 2 - anchor[0]) * (x + dx + w / 2 - anchor[0])) + ((y + dy + h / 2 - anchor[1]) * (y + dy + h / 2 - anchor[1]))));
				
				if(distance >= maxDistance) { // check if at the end of rope
					swinging = true;
					maxDistance = Math.abs(Math.sqrt(((x + w / 2 - anchor[0]) * (x + w / 2 - anchor[0])) + ((y + h / 2 - anchor[1]) * (y + h / 2 - anchor[1]))));
					
					angle = Math.acos((x + w / 2 - anchor[0]) / maxDistance); // set angle from player to anchor
					if((y + h / 2 - anchor[1]) < 0) {angle = -angle;} // fix angle if above anchor (don't ask why it's needed, i have no idea - don't question it, don't remove it)
					
					da = Physics.linearDelta2AngularDelta(angle, dx, dy); // set delta angular to be proportional to delta x and delta y
					dx = 0;
					dy = 0;
				}
			}
			else {
				if((y + h / 2 - anchor[1]) < 0 && da <= 5 && da >= -5) { // not sure why 5, but using trial and error it just feels right
					swinging = false;
					
					// set delta x and delta y to be proportional to delta angular
					double[] dl = Physics.angularDelta2LinearDelta(angle, da);
					
					dx = dl[0];
					dy = dl[1];
					da = 0;
				}
			}
		}
		else {
			if(swinging) { // currently but shouldn't be
				swinging = false;
				
				// set delta x and delta y to be proportional to delta angular
				double[] dl = Physics.angularDelta2LinearDelta(angle, da);
				
				dx = dl[0];
				dy = dl[1];
				da = 0;
			}
		}
		
		// apply gravity to dy
		if(!swinging) {dy += LINEAR_GRAVITY;} // easier when not swinging
		else { // changes amount of force applied to da depending on how close to being parallel with the ground the player is
			double xGrav = Math.cos(angle);
			double yGrav = Math.sin(angle);
			
			double l1 = Math.sqrt(xGrav * xGrav + yGrav * yGrav); 
			double nx1 = xGrav / l1;
			double ny1 = yGrav / l1;
			
			double nx2 = 0;
			double ny2 = -1;
			
			double cp = (nx1 * ny2) - (ny1 * nx2);
			
			//   0% = directly above or below anchor
			// 100% = directly left or right to anchor
			
			//   0% = anchor[1] (+ or -) maxDistance
			// 100% = anchor[1]
			
			double gravOffset = cp;
			
			da += ANGULAR_GRAVITY * gravOffset;
			
			// apply air friction/resistance
			if(da < 0) {da += A_DECEL;}
			else if(da > 0) {da -= A_DECEL;}
		}
		
		// limit speed (atm speed not limited, just won't accelerate if above max speed)
		/*if(dx < -X_MAX) {dx = -X_MAX;}
		if(dx > X_MAX) {dx = X_MAX;}
		if(dy < -Y_MAX) {dy = -Y_MAX;}
		if(dy > Y_MAX) {dy = Y_MAX;}
		if(da < -A_MAX) {da = -A_MAX;}
		if(da > A_MAX) {da = A_MAX;}*/
		
		// step angle with da
		if(swinging) {
			angle -= da / maxDistance;
			
			// make sure angle isn't outside of radian range
			if(angle > Math.PI * 2) {angle -= Math.PI * 2;}
			if(angle < 0) {angle += Math.PI * 2;}
		}
		
		// step x with dx
		if(!swinging) {x += dx;}
		else {x = Math.cos(angle) * maxDistance + anchor[0] - w / 2;}
		
		// pop player out of solids
		collisionResponse(tiles);
		
		// step y with dy
		if(!swinging) {y += dy;}
		else {y = Math.sin(angle) * maxDistance + anchor[1] - h / 2;}
		
		// pop player out of solids
		collisionResponse(tiles);
		
		// change color if in solid
		boolean collided = false;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(x, y, w, h, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {collided = true;}}
		
		if(collided) {color = Color.GREEN;}
		else {color = Color.WHITE;}
	}
	
	public void draw(Graphics2D graphics) {
		// draw player rect
		graphics.setColor(color);
		graphics.fillRect((int)x, (int)y, w, h);
		
		// change rope color if at max distance
		if(swinging) {color = Color.GREEN;}
		else {color = Color.WHITE;}
		
		// draw rope + anchor circle
		graphics.setColor(color);
		if(canSwing) {
			graphics.fillOval((int)anchor[0] - 5, (int)anchor[1] - 5, 10, 10);
			graphics.drawLine((int)x + w / 2, (int)y + h / 2, (int)anchor[0], (int)anchor[1]);
			
		}
		
		// draw mouse raw (line between center of player and mouse pos)
		graphics.setColor(Color.GRAY);
		if(mouseInWindow) {
			if(!canSwing) {graphics.drawLine((int)x + w / 2, (int)y + h / 2, (int)xMouse, (int)yMouse);}
			
			graphics.setColor(Color.CYAN);
			if(!canSwing && canHook) {graphics.drawLine((int)x + w / 2, (int)y + h / 2, (int)xMouseRay, (int)yMouseRay);}
		}
	}
	
	private void collisionResponse(int[][] tiles) {
		double[] pop = new double[]{0, 0};
		double[] popA = new double[]{0, 0, 0};
		
		// pop out of solids, but only if collided with something
		boolean collided = false;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(x, y, w, h, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {collided = true;}}
		
		if(collided) {
			if(!swinging) { // linear pop out (not swinging)
				pop = Collision.popOut(x, y, (double)w, (double)h, tiles);
				x += pop[0];
				y += pop[1];
			}
			else { // angular pop out (swinging)
				popA = Collision.angularPopOut(x, y, (double)w, (double)h, anchor[0], anchor[1], angle, maxDistance, tiles);
				x += popA[0];
				y += popA[1];
				
				angle = Math.acos((x + w / 2 - anchor[0]) / maxDistance);
				if((y + h / 2 - anchor[1]) < 0) {angle = -angle;}
			}
		}
		
		// change speed if collided
		if(!swinging) {
			if(pop[0] != 0) {dx = 0;}
			if(pop[1] != 0) {dy = 0;}
		}
		else {
			if(popA[0] != 0 || popA[1] != 0) {
				double[] dl = Physics.angularDelta2LinearDelta(angle, da);
				
				//System.out.println("da = " + da);
				
				da = 0;
				
				dx = dl[0];
				dy = dl[1];
				
				pop = Collision.popOut(x + dx, y + dy, (double)w, (double)h, tiles);
				
				if(pop[0] != 0) {dx = 0;}
				if(pop[1] != 0) {dy = 0;}
				
				distance = Math.abs(Math.sqrt(((x + dx + w / 2 - anchor[0]) * (x + dx + w / 2 - anchor[0])) + ((y + dy + h / 2 - anchor[1]) * (y + dy + h / 2 - anchor[1]))));
				
				if(distance < maxDistance) {swinging = false;}// check if NOT at the end of rope
				else {
					dx = 0;
					dy = 0;
				}
				
				//System.out.println();
			}
		}
	}
	
	public void keyPressed(int key) {
		switch(key) {
			case(VK_W): up = true; break;
			case(VK_S): down = true; break;
			case(VK_A): left = true; break;
			case(VK_D): right = true; break;
			case(VK_SPACE): space = true; break;
			//case VK_SHIFT: shift = true; break;
		}
	}
	public void keyReleased(int key) {
		switch(key) {
			case(VK_W): up = false; break;
			case(VK_S): down = false; break;
			case(VK_A): left = false; break;
			case(VK_D): right = false; break;
			case(VK_SPACE): space = false; break;
			case(VK_R): reset = true; break;
			//case VK_SHIFT: shift = false; break;
		}
	}

	public void mouseClicked(MouseEvent mouse) {}
	public void mouseEntered(MouseEvent mouse) {mouseInWindow = true;}
	public void mouseExited(MouseEvent mouse) {mouseInWindow = false;}
	public void mousePressed(MouseEvent mouse) {
		if(canHook) {
			anchor = new double[]{xMouseRay, yMouseRay};
			maxDistance = Math.abs(Math.sqrt(((x + w / 2 - anchor[0]) * (x + w / 2 - anchor[0])) + ((y + h / 2 - anchor[1]) * (y + h / 2 - anchor[1]))));
			
			canSwing = true;
		}
	}
	public void mouseReleased(MouseEvent mouse) {canSwing = false;}
}
