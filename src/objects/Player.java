package objects;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

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
	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void update(int[/* index */][/* x, y, w, h */] tiles) {
		ground = false;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(x, y + h, w, 1, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {ground = true;}}
		
		if(left && !right && dx > -X_MAX) {
			dx -= X_ACCEL;
			if(dx > 0) {dx -= X_DECEL;} // additional speed if going against current velocity
		}
		if(right && !left && dx < X_MAX) {
			dx += X_ACCEL;
			if(dx < 0) {dx += X_DECEL;} // additional speed if going against current velocity
		}
		
		if(ground) {
			if(!left && !right || left && right) {
				if(dx < -X_ACCEL) {dx += X_DECEL;}
				else if(dx > X_ACCEL) {dx -= X_DECEL;}
				else {dx = 0;}
			}
			
			if(canSwing) {maxDistance = Math.abs(Math.sqrt(((x + w / 2 - anchor[0]) * (x + w / 2 - anchor[0])) + ((y + h / 2 - anchor[1]) * (y + h / 2 - anchor[1]))));}
			
			if(space && canJump) {
				canJump = false;
				jumping = true;
				ground = false;
				
				dy -= JUMP_FORCE;
			}
			if(!space) {canJump = true;}
		}
		else {
			if(!swinging) {
				if(jumping) {
					if(!space) {dy += JUMP_DECEL;}

					if(dy >= 0) {jumping = false;}
				}
			}
			else {jumping = false;}
		}
		
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
				System.out.println(da);
				
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
		if(!swinging) {dy += LINEAR_GRAVITY;}
		else {
			double xGrav = Math.cos(angle);
			double yGrav = Math.sin(angle);
			
			double l1 = Math.sqrt(xGrav * xGrav + yGrav * yGrav); 
			double nx1 = xGrav / l1;
			double ny1 = yGrav / l1;
			
			double nx2 = 0;
			double ny2 = -1;
			
			double cp = (nx1 * ny2) - (ny1 * nx2);
			
			//   0% = anchor[1] (+ or -) maxDistance
			// 100% = anchor[1]
			
			double gravOffset = cp;
			
			System.out.println("grav = " + (ANGULAR_GRAVITY * gravOffset));
			
			da += ANGULAR_GRAVITY * gravOffset;
			
			if(da < 0) {da += A_DECEL;}
			else if(da > 0) {da -= A_DECEL;}
		}
		
		// limit speed
		/*if(dx < -X_MAX) {dx = -X_MAX;}
		if(dx > X_MAX) {dx = X_MAX;}
		if(dy < -Y_MAX) {dy = -Y_MAX;}
		if(dy > Y_MAX) {dy = Y_MAX;}
		if(da < -A_MAX) {da = -A_MAX;}
		if(da > A_MAX) {da = A_MAX;}*/
		
		if(swinging) {
			angle -= da / maxDistance;
			
			if(angle > Math.PI * 2) {angle -= Math.PI * 2;}
			if(angle < 0) {angle += Math.PI * 2;}
		}
		
		// step x
		if(!swinging) {x += dx;}
		else {x = Math.cos(angle) * maxDistance + anchor[0] - w / 2;}
		
		// pop player out of solids
		collisionResponse(tiles);
		
		// step y
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
		graphics.setColor(color);
		graphics.fillRect((int)x, (int)y, w, h);
		
		if(swinging) {color = Color.GREEN;}
		else {color = Color.WHITE;}
		
		graphics.setColor(color);
		if(canSwing) {graphics.drawLine((int)x + w / 2, (int)y + h / 2, (int)anchor[0], (int)anchor[1]);}
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
			//case VK_SHIFT: shift = false; break;
		}
	}

	public void mouseClicked(MouseEvent mouse) {}
	public void mouseEntered(MouseEvent mouse) {}
	public void mouseExited(MouseEvent mouse) {}
	public void mousePressed(MouseEvent mouse) {
		anchor = new double[]{mouse.getX(), mouse.getY()};
		maxDistance = Math.abs(Math.sqrt(((x + w / 2 - anchor[0]) * (x + w / 2 - anchor[0])) + ((y + h / 2 - anchor[1]) * (y + h / 2 - anchor[1]))));
		
		canSwing = true;
	}
	public void mouseReleased(MouseEvent mouse) {canSwing = false;}
}
