package main;

public class Collision {
	/*public static int    getUp(int y1, int h1, int y2, int h2) {return ((int)Math.floor(((y2 >> 12) +  0) / 16) * 16 << 12) - (y1 + (h1 << 12));}
	public static int  getDown(int y1, int h1, int y2, int h2) {return ((int)Math.floor(((y2 >> 12) + h2) / 16) * 16 << 12) -  y1              ;}
	public static int  getLeft(int x1, int w1, int x2, int w2) {return ((int)Math.floor(((x2 >> 12) +  0) / 16) * 16 << 12) - (x1 + (w1 << 12));}
	public static int getRight(int x1, int w1, int x2, int w2) {return ((int)Math.floor(((x2 >> 12) + w2) / 16) * 16 << 12) -  x1              ;}*/
	
	public static boolean checkCollision(double x1, double y1, double w1, double h1, double x2, double y2, double w2, double h2) {
		return(
			(x1 >= x2 && x1 < x2 + w2 || x1 + w1 > x2 && x1 + w1 <= x2 + w2) &&
			(y1 >= y2 && y1 < y2 + h2 || y1 + h1 > y2 && y1 + h1 <= y2 + h2) ||
			(x2 >= x1 && x2 < x1 + w1 || x2 + w2 > x1 && x2 + w2 <= x1 + w1) &&
			(y2 >= y1 && y2 < y1 + h1 || y2 + h2 > y1 && y2 + h2 <= y1 + h1)
		);
	}
	
	public static double[] popOut(double x, double y, double w, double h, double[/* index */][/* x, y, w, h */] tiles) {
		int collidedTiles = 0;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(x, y, w, h, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {collidedTiles++;}}
		double[][] collisionData = new double[collidedTiles][4];
		int collisionIndex = 0;
		for(int i = 0; i < tiles.length; i++) {
			if(Collision.checkCollision(x, y, w, h, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {
				collisionData[collisionIndex][0] = tiles[i][0];
				collisionData[collisionIndex][1] = tiles[i][1];
				collisionData[collisionIndex][2] = tiles[i][2];
				collisionData[collisionIndex][3] = tiles[i][3];
				collisionIndex++;
			}
		}
		
		return(realPopOut(x, y, w, h, collisionData));
	}
	public static double[] popOut(double x, double y, double w, double h, int[/* index */][/* x, y, w, h */] tiles) {
		int collidedTiles = 0;
		for(int i = 0; i < tiles.length; i++) {if(Collision.checkCollision(x, y, w, h, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {collidedTiles++;}}
		double[][] collisionData = new double[collidedTiles][4];
		int collisionIndex = 0;
		for(int i = 0; i < tiles.length; i++) {
			if(Collision.checkCollision(x, y, w, h, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {
				collisionData[collisionIndex][0] = tiles[i][0];
				collisionData[collisionIndex][1] = tiles[i][1];
				collisionData[collisionIndex][2] = tiles[i][2];
				collisionData[collisionIndex][3] = tiles[i][3];
				collisionIndex++;
			}
		}
		
		return(realPopOut(x, y, w, h, collisionData));
	}
	
	static double[] realPopOut(double x, double y, double w, double h, double[/* index */][/* x, y, w, h */] tiles) {
		double[/* index = 4 possible movements (up, down, left, right) per tile */][/* x movement, y movement */] offsets = new double[tiles.length * 4][2];
		
		for(int i = 0; i < offsets.length; i += 4) {
			offsets[i + 0][0] = tiles[(int)Math.floor(i / 4)][0] - (x + w);                                // left (negative number)  - right side of box (x + w) moves to left side of tile (tile.x)
			offsets[i + 0][1] = 0;
			offsets[i + 1][0] = (tiles[(int)Math.floor(i / 4)][0] + tiles[(int)Math.floor(i / 4)][2]) - x; // right (positive number) - left side of box (x) moves to right side of tile (tile.x + tile.w)
			offsets[i + 1][1] = 0;
			offsets[i + 2][0] = 0;
			offsets[i + 2][1] = tiles[(int)Math.floor(i / 4)][1] - (y + h);                                // up (negative number)    - bottom side of box (y + h) moves to top side of tile (tile.y)
			offsets[i + 3][0] = 0;
			offsets[i + 3][1] = (tiles[(int)Math.floor(i / 4)][1] + tiles[(int)Math.floor(i / 4)][3]) - y; // down (positive number)  - top side of box (y) moves to bottom side of tile (tile.y + tile.h)
		}
		
		double smallestMovement = 0;
		int smallestIndex = -1;
		for(int i = 0; i < offsets.length; i++) {
			double tempMove = Math.sqrt((offsets[i][0] * offsets[i][0]) + (offsets[i][1] * offsets[i][1]));
			
			if(Math.abs(tempMove) < Math.abs(smallestMovement) || smallestIndex == -1) {
				boolean good = true;
				for(int o = 0; o < tiles.length; o++) {if(checkCollision(x + offsets[i][0], y + offsets[i][1], w, h, tiles[o][0], tiles[o][1], tiles[o][2], tiles[o][3])) {good = false;}}
			
				if(good) {
					smallestMovement = tempMove;
					smallestIndex = i;
					
					//System.out.println("Smallest Move Set - " + offsets[smallestIndex][0] + ", " + offsets[smallestIndex][1]);
				}
			}
		}
		
		if(smallestIndex != -1) {
			System.out.println();
			return(offsets[smallestIndex]);
		}
		
		// if smallest index = -1
		return(new double[]{0, 0}); // returns [x movement, y movement]
	}
}
