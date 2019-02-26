package objects;

import main.Collision;
import main.Physics;

public class CollisionFunctions {
	public static boolean checkCollision(int[][] tiles) {
		// checks if colliding with solid tiles at current position
		
		boolean collided = false;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(Player.x, Player.y, Player.w, Player.h, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {collided = true;}}
	
		return(collided);
	}
	public static boolean checkGround(int[][] tiles) {
		// set ground flag if row of pixels below Player is colliding with tile
		
		boolean ground = false;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(Player.x, Player.y + Player.h, Player.w, 1, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {ground = true;}}
		
		return(ground);
	}
	public static boolean checkLeft(int[][] tiles) {
		// set left flag if row of pixels to the left of the Player is colliding with tile
		
		boolean left = false;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(Player.x - 1, Player.y, 1, Player.h, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {left = true;}}
		
		return(left);
	}
	public static boolean checkRight(int[][] tiles) {
		// set left flag if row of pixels to the right of the Player is colliding with tile
		
		boolean right = false;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(Player.x + Player.w, Player.y, 1, Player.h, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {right = true;}}
		
		return(right);
	}
	
	public static int[][] getCollidingTiles(int[][] tiles) {
		int numCollidingTiles = 0;
		for(int i = 0; i < tiles.length; i++) {if(Collision.lineRect(Player.x + Player.w / 2, Player.y + Player.h / 2, Player.xAnchor, Player.yAnchor, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {numCollidingTiles++;}}
		
		int[][] collidingTiles = new int[numCollidingTiles][4];
		int index = 0;
		
		for(int i = 0; i < tiles.length; i++) {
			if(Collision.lineRect(Player.x + Player.w / 2, Player.y + Player.h / 2, Player.xAnchor, Player.yAnchor, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {
				collidingTiles[index] = tiles[i];
				index++;
			}
		}
		
		return(collidingTiles);
	}

	public static void collisionResponse(int[][] tiles) {
		double[] pop = new double[]{0, 0};
		double[] popA = new double[]{0, 0, 0};
		
		// pop out of solids, but only if collided with something
		if(checkCollision(tiles)) {
			if(!Player.swinging) { // linear pop out (not swinging)
				pop = Collision.popOut(Player.x, Player.y, (double)Player.w, (double)Player.h, tiles);
				Player.x += pop[0];
				Player.y += pop[1];
			}
			else { // angular pop out (swinging)
				popA = Collision.angularPopOut(Player.x, Player.y, (double)Player.w, (double)Player.h, Player.xAnchor, Player.yAnchor, Player.angle, Player.maxDistance, tiles);
				Player.x += popA[0];
				Player.y += popA[1];
				
				Player.angle = Math.acos((Player.x + Player.w / 2 - Player.xAnchor) / Player.maxDistance);
				if((Player.y + Player.h / 2 - Player.yAnchor) < 0) {Player.angle = -Player.angle;}
			}
		}
		
		// change speed if collided
		if(!Player.swinging) {
			if(pop[0] != 0) {Player.dx = 0;}
			if(pop[1] != 0) {Player.dy = 0;}
		}
		else {
			if(popA[0] != 0 || popA[1] != 0) {
				double[] dl = Physics.angularDelta2LinearDelta(Player.angle, Player.da);
				
				Player.da = 0;
				
				Player.dx = dl[0];
				Player.dy = dl[1];
				
				pop = Collision.popOut(Player.x + Player.dx, Player.y + Player.dy, (double)Player.w, (double)Player.h, tiles);
				
				if(pop[0] != 0) {Player.dx = 0;}
				if(pop[1] != 0) {Player.dy = 0;}
				
				Player.distance = Math.abs(Math.sqrt(((Player.x + Player.dx + Player.w / 2 - Player.xAnchor) * (Player.x + Player.dx + Player.w / 2 - Player.xAnchor)) + ((Player.y + Player.dy + Player.h / 2 - Player.yAnchor) * (Player.y + Player.dy + Player.h / 2 - Player.yAnchor))));
				
				if(Player.distance < Player.maxDistance) {Player.swinging = false;} // check if NOT at the end of rope
				else {
					Player.dx = 0;
					Player.dy = 0;
				}
			}
		}
	}
}
