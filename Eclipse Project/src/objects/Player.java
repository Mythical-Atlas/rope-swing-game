package objects;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import main.Collision;
import main.Physics;

public class Player {
	public static final double X_ACCEL = 0.1;
	public static final double A_ACCEL = 0.1;
	
	public static final double X_DECEL = 0.2;
	public static final double A_DECEL = 0.01;
	
	public static final double X_MAX = 10;
	public static final double Y_MAX = 10;
	public static final double A_MAX = 10;
	
	public static final double JUMP_FORCE = 10;
	public static final double JUMP_DECEL = 1;
	
	public static final double LINEAR_GRAVITY  = 0.3;
	public static final double ANGULAR_GRAVITY = 0.2;
	
	public static final double LINEAR_SLIDE = 0.4;
	public static final double MAX_SLIDE    = 2;
	
	public static final double X_WALL_JUMP = 5;
	public static final double Y_WALL_JUMP = 7.5;
	
	public static double x, y;
	public static double dx, dy, da; // dx = delta x (x speed), dy = delta y (y speed), da = delta angular (angular speed)
	
	public static int w = 16;
	public static int h = 16;
	
	public static Color color = Color.WHITE;
	
	public static boolean up, down, left, right, space;
	public static boolean leftMouse, rightMouse;
	
	public static boolean ground;
	
	public static boolean jumping;
	public static boolean canJump;
	
	// closest anchor
	public static double xAnchor, yAnchor;
	
	public static double distance;
	public static double maxDistance;
	public static double[][] anchors; // all OTHER anchors
	public static double angle;
	
	public static boolean swinging;
	public static boolean mouseInWindow;
	public static boolean canHook;
	public static boolean reset;
	
	public static boolean hangingShort;
	public static double previousDistance;
	
	public static double xMouse, yMouse;
	public static double xMouseRay, yMouseRay;
	public static double xTempMouse;
	public static double yTempMouse;
	
	public static double ox, oy;
	
	public static int xOffset, yOffset;
	
	public static boolean canWallJump;
	
	public static boolean canYank;
	
	// -1 = left
	//  0 = not sliding on wall
	//  1 = right
	public static int sliding;
	
	public Player(int x, int y) {
		Player.x = x;
		Player.y = y;
		
		ox = x;
		oy = y;
	}
	
	public static void update(int[/* index */][/* x, y, w, h */] tiles, JFrame frame) {
		MiscFunctions.scrollScreen();
		
		// finds mouse pos and finds shortest line between player and solid tile 
		if(mouseInWindow) {MiscFunctions.getMousePos(tiles, frame);}
		
		if(reset) {MiscFunctions.reset();}// reset player (useful if debugging and no stage bounds)
		
		if(leftMouse) {
			if(up) {maxDistance--;}
			if(down) {maxDistance++;}
		}
		
		if(maxDistance < w) {maxDistance = w;}
		
		// set ground flag if row of pixels below player is colliding with tile
		ground = CollisionFunctions.checkGround(tiles);
		
		if(ground) {
			// apply friction
			dx = MovementFunctions.linearFriction();
			
			// jump
			if(space && canJump) {MovementFunctions.jump();}
			if(!space) {canJump = true;} // allow to jump with space, but only if on ground and space is released
		}
		else {
			// apply gravity to dy
			if(!swinging) {
				// applies linear gravity (must check if not swinging before calling this function)
				// easier when not swinging
				
				if(da < Y_MAX) {dy += LINEAR_GRAVITY;}
			}
			else {da = MovementFunctions.angularGravity();}
			
			MovementFunctions.slideIfAble(tiles);
			MovementFunctions.wallJumpIfAble();
			
			// step jump
			// holding space after jumping allows for extra height (more like allows default height - releasing space decelerates player quickly)
			if(!swinging) {
				if(jumping) {
					if(!space) {dy += JUMP_DECEL;}

					if(dy >= 0) {jumping = false;}
				}
			}
			else {jumping = false;}
			
			canJump = false;
		}
		
		if(swinging) { // move player while swinging
			da = MovementFunctions.angularMovement();
			
			if(space) {
				if(canYank) {
					MovementFunctions.setNotSwinging();
					
					double xMouseDist = x + w / 2 - xAnchor;
					double yMouseDist = y + h / 2 - yAnchor;
					double mouseAngle = Math.atan(yMouseDist / xMouseDist);
					
					if(xMouseDist > 0) {mouseAngle += Math.PI;}
					
					if(mouseAngle > Math.PI * 2) {mouseAngle -= Math.PI * 2;}
					if(mouseAngle < 0) {mouseAngle += Math.PI * 2;}
					
					dx += Math.cos(mouseAngle) * JUMP_FORCE;
					dy += Math.sin(mouseAngle) * JUMP_FORCE;
				}
			}
			else {canYank = true;}
			
		}
		else { // move player when not swinging
			dx = MovementFunctions.linearMovement();
			canYank = false;
		}
		
		// check if swinging state should be changed
		if(leftMouse) {
			// check if line between player and closest anchor is being broken by a tile
			int[][] collidingTiles = CollisionFunctions.getCollidingTiles(tiles);
			
			// if yes, wrap anchor
			// xAnchor and yAnchor represent CLOSEST anchor, anchors[0] is the actual base anchor (the one set by the mouse)
			if(collidingTiles.length > 0) {}
			
			if(!swinging) {MovementFunctions.setSwinging();} // not currently swinging but should be
			else { // currently swinging but shouldn't be
				// check if above anchor and going to slow
				// if so, set not swinging
				if((y + h / 2 - yAnchor) < 0 && da <= 5 && da >= -5) { // not sure why 5, but using trial and error it just feels right
					MovementFunctions.setNotSwinging();
				}
			}
		}
		else {if(swinging) {MovementFunctions.setNotSwinging();}} // currently swinging but shouldn't be
		
		// step angle with da
		if(swinging) {angle = MovementFunctions.changeAngle();}
		
		if(!swinging) {x += dx;} // step x with dx
		else {x = Math.cos(angle) * maxDistance + xAnchor - w / 2;} // step x with da
		
		CollisionFunctions.collisionResponse(tiles); // pop player out of solids
		
		if(!swinging) {y += dy;} // step y with dy
		else {y = Math.sin(angle) * maxDistance + yAnchor - h / 2;} // step y with da
		
		CollisionFunctions.collisionResponse(tiles); // pop player out of solids
		
		// change color if in solid
		if(CollisionFunctions.checkCollision(tiles)) {color = Color.GREEN;}
		else {color = Color.WHITE;}
	}
	
	public static void draw(Graphics2D graphics) {
		// draw player square
		graphics.setColor(color);
		graphics.fillRect((int)x + xOffset, (int)y + yOffset, w, h);
		
		// change rope color if at max distance
		if(swinging) {color = Color.GREEN;}
		else {color = Color.WHITE;}
		
		// draw rope + anchor circle
		graphics.setColor(color);
		if(leftMouse) {
			graphics.fillOval((int)xAnchor - 5 + xOffset, (int)yAnchor - 5 + yOffset, 10, 10);
			graphics.drawLine((int)x + w / 2 + xOffset, (int)y + h / 2 + yOffset, (int)xAnchor + xOffset, (int)yAnchor + yOffset);
		}
		
		// draw mouse raw (line between center of player and mouse pos)
		graphics.setColor(Color.GRAY);
		if(mouseInWindow) {
			if(!leftMouse) {graphics.drawLine((int)x + w / 2 + xOffset, (int)y + h / 2 + yOffset, (int)xMouse + xOffset, (int)yMouse + yOffset);}
			
			/*graphics.setColor(Color.MAGENTA);
			graphics.drawLine((int)x + w / 2 + xOffset, (int)y + h / 2 + yOffset, (int)xTempMouse + xOffset, (int)yTempMouse + yOffset);*/
			
			graphics.setColor(Color.CYAN);
			if(!leftMouse && canHook) {graphics.drawLine((int)x + w / 2 + xOffset, (int)y + h / 2 + yOffset, (int)xMouseRay + xOffset, (int)yMouseRay + yOffset);}
		}
	}
	
	public static void keyPressed(int key) {
		switch(key) {
			//case(VK_W): up = true; break;
			//case(VK_S): down = true; break;
			case(VK_A): left = true; break;
			case(VK_D): right = true; break;
			case(VK_SPACE): space = true; break;
		}
	}
	public static void keyReleased(int key) {
		switch(key) {
			//case(VK_W): up = false; break;
			//case(VK_S): down = false; break;
			case(VK_A): left = false; break;
			case(VK_D): right = false; break;
			case(VK_SPACE): space = false; break;
			case(VK_R): reset = true; break; // only resets on release, so it doesn't do it every frame while the R key is held down
		}
	}

	public static void mouseClicked(MouseEvent mouse) {}
	public static void mouseEntered(MouseEvent mouse) {mouseInWindow = true;}
	public static void mouseExited(MouseEvent mouse) {mouseInWindow = false;}
	public static void mousePressed(MouseEvent mouse) {
		if(canHook) {
			if(mouse.getButton() == MouseEvent.BUTTON1) {
				xAnchor = xMouseRay;
				yAnchor = yMouseRay;
				maxDistance = Math.abs(Math.sqrt(((x + w / 2 - xAnchor) * (x + w / 2 - xAnchor)) + ((y + h / 2 - yAnchor) * (y + h / 2 - yAnchor))));
				
				leftMouse = true;
			}
		}
		
		if(mouse.getButton() == MouseEvent.BUTTON3) {
			if(leftMouse) {
				if(!swinging) {
					hangingShort = true;
					previousDistance = maxDistance;
					maxDistance = Math.abs(Math.sqrt(((x + w / 2 - xAnchor) * (x + w / 2 - xAnchor)) + ((y + h / 2 - yAnchor) * (y + h / 2 - yAnchor))));
				}
			}
			else {
				if(ground || sliding != 0) {
					if(canHook) {
						double xMouseDist = x + w / 2 - xMouse;
						double yMouseDist = y + h / 2 - yMouse;
						double mouseAngle = Math.atan(yMouseDist / xMouseDist);
						
						if(xMouseDist > 0) {mouseAngle += Math.PI;}
						
						if(mouseAngle > Math.PI * 2) {mouseAngle -= Math.PI * 2;}
						if(mouseAngle < 0) {mouseAngle += Math.PI * 2;}
						
						dx = Math.cos(mouseAngle) * JUMP_FORCE;
						dy = Math.sin(mouseAngle) * JUMP_FORCE;
					}
				}
			}
		}
	}
	public static void mouseReleased(MouseEvent mouse) {
		if(mouse.getButton() == MouseEvent.BUTTON1) {
			leftMouse = false;
			hangingShort = false;
		}
		if(mouse.getButton() == MouseEvent.BUTTON3) {
			if(hangingShort && leftMouse) {
				hangingShort = false;
				maxDistance = previousDistance;
				if(swinging) {MovementFunctions.setNotSwinging();}
			}
		}
	}
}
