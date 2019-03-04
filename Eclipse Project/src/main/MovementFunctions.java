package main;

import objects.Player;

public class MovementFunctions {
	public static double linearFriction() {
		// apply linear friction when not holding left or right (or pressing both)
		// slows Player down
		
		double dx = Player.dx;
		
		if(!Player.left && !Player.right || Player.left && Player.right) {
			if(dx < -Player.X_ACCEL) {
				dx += Player.X_DECEL;
				Player.stateIndex = Player.RUN;
			}
			else if(dx > Player.X_ACCEL) {
				dx -= Player.X_DECEL;
				Player.stateIndex = Player.RUN;
			}
			else {
				dx = 0;
				Player.stateIndex = Player.IDLE;
			}
		}
		
		return(dx);
	}
	public static double linearMovement() {
		// move Player when not swinging
		
		double dx = Player.dx;
		
		if(Player.ground && dx != 0) {Player.stateIndex = Player.RUN;}
		
		if(Player.left && !Player.right && dx > -Player.X_MAX) {
			if(Player.ground) {
				if(Player.facingRight) {
					if(dx == 0) {Player.stateIndex = Player.IDLE_TURN;}
					else {Player.stateIndex = Player.RUN_TURN;}
				}
				
				Player.facingRight = false;
			}
			
			dx -= Player.X_ACCEL;
			
			if(!Player.ground) {dx -= Player.X_ACCEL;} // additional speed if in air
			if(dx > 0 && Player.ground) {dx -= Player.X_DECEL;} // additional speed if going against current velocity
		}
		if(Player.right && !Player.left && dx < Player.X_MAX) {
			if(Player.ground) {
				if(!Player.facingRight) {
					if(dx == 0) {Player.stateIndex = Player.IDLE_TURN;}
					else {Player.stateIndex = Player.RUN_TURN;}
				}
				
				Player.facingRight = true;
			}
			
			dx += Player.X_ACCEL;
			
			if(!Player.ground) {dx += Player.X_ACCEL;} // additional speed if in air
			if(dx < 0 && Player.ground) {dx += Player.X_DECEL;} // additional speed if going against current velocity
		}
		
		return(dx);
	}
	
	public static double angularMovement() {
		// moves Player while swinging depending on keys held down
		
		double da = Player.da;
		
		if(Player.left && Player.da > -Player.A_MAX) {
			da -= Player.A_ACCEL;
			Player.facingRight = false;
		}
		if(Player.right && da < Player.A_MAX) {
			da += Player.A_ACCEL;
			Player.facingRight = true;
		}
		
		return(da);
	}
	public static double angularGravity() {
		// applies angular gravity (must check if swinging before calling this function)
		// changes amount of force applied to da depending on how close to being parallel with the ground the Player is
		
		double xGrav = Math.cos(Player.angle);
		double yGrav = Math.sin(Player.angle);
		double da = Player.da;
		
		double l1 = Math.sqrt(xGrav * xGrav + yGrav * yGrav); 
		double nx1 = xGrav / l1;
		double ny1 = yGrav / l1;
		
		double nx2 = 0;
		double ny2 = -1;
		
		double cp = (nx1 * ny2) - (ny1 * nx2);
		
		//   0% = directly above or below anchor
		// 100% = directly left or right to anchor
		
		//   0% = yAnchor (+ or -) maxDistance
		// 100% = yAnchor
		
		double gravOffset = cp;
		
		da += Player.ANGULAR_GRAVITY * gravOffset;
		
		// apply air friction/resistance
		if(da < 0) {da += Player.A_DECEL;}
		else if(da > 0) {da -= Player.A_DECEL;}
		
		return(da);
	}
	
	public static double changeAngle() {
		// step angle with da
		
		double angle = Player.angle - Player.da / Player.maxDistance;
		
		// make sure angle isn't outside of radian range
		if(angle > Math.PI * 2) {angle -= Math.PI * 2;}
		if(angle < 0) {angle += Math.PI * 2;}
		
		return(angle);
	}

	public static void jump() {
		Player.canJump = false;
		Player.jumping = true;
		Player.ground = false;
		Player.canWallJump = false;
			
		Player.dy -= Player.JUMP_FORCE;
	}

	public static void slideIfAble(int[][] tiles) {
		if(CollisionFunctions.checkLeft(tiles) && /*Player.left &&*/ !Player.ground) {Player.sliding = -1;}
		if(CollisionFunctions.checkRight(tiles) && /*Player.right &&*/ !Player.ground) {Player.sliding = 1;}
		if(!CollisionFunctions.checkLeft(tiles) && !CollisionFunctions.checkRight(tiles) || Player.ground) {Player.sliding = 0;}
		
		if(Player.sliding != 0) {if(Player.dy > Player.MAX_SLIDE) {Player.dy = Player.MAX_SLIDE;}}
	}
	public static void wallJumpIfAble() {
		if(Player.sliding == -1) {
			if(Player.space) {
				if(Player.canWallJump){
					Player.jumping = true;
					Player.canWallJump = false;
					Player.canJump = false;
					Player.swinging = false;
					
					Player.dy -= Player.Y_WALL_JUMP;
					Player.dx += Player.X_WALL_JUMP;
				}
			}
			else {Player.canWallJump = true;}
		}
		else if(Player.sliding == 1) {
			if(Player.space) {
				if(Player.canWallJump){
					Player.jumping = true;
					Player.canWallJump = false;
					Player.canJump = false;
					Player.swinging = false;
					
					Player.dy -= Player.Y_WALL_JUMP;
					Player.dx -= Player.X_WALL_JUMP;
				}
			}
			else {Player.canWallJump = true;}
		}
		else {Player.canWallJump = false;}
	}

	public static void setSwinging() {
		Player.distance = Math.abs(Math.sqrt(((Player.x + Player.dx + Player.w / 2 - Player.xAnchor) * (Player.x + Player.dx + Player.w / 2 - Player.xAnchor)) + ((Player.y + Player.dy + Player.h / 2 - Player.yAnchor) * (Player.y + Player.dy + Player.h / 2 - Player.yAnchor))));
		
		if(Player.distance >= Player.maxDistance) { // check if at the end of rope
			Player.swinging = true;
			Player.maxDistance = Math.abs(Math.sqrt(((Player.x + Player.w / 2 - Player.xAnchor) * (Player.x + Player.w / 2 - Player.xAnchor)) + ((Player.y + Player.h / 2 - Player.yAnchor) * (Player.y + Player.h / 2 - Player.yAnchor))));
			
			Player.angle = Math.acos((Player.x + Player.w / 2 - Player.xAnchor) / Player.maxDistance); // set angle from player to anchor
			if((Player.y + Player.h / 2 - Player.yAnchor) < 0) {Player.angle = -Player.angle;} // fix angle if above anchor (don't ask why it's needed, i have no idea - don't question it, don't remove it)
			
			Player.da = Physics.linearDelta2AngularDelta(Player.angle, Player.dx, Player.dy); // set delta angular to be proportional to delta x and delta y
			Player.dx = 0;
			Player.dy = 0;
		}
	}
	public static void setNotSwinging() {
		Player.swinging = false;
		
		// set delta x and delta y to be proportional to delta angular
		double[] dl = Physics.angularDelta2LinearDelta(Player.angle, Player.da);
		
		Player.dx = dl[0];
		Player.dy = dl[1];
		Player.da = 0;
	}
}
