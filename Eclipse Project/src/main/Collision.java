package main;

public class Collision {
	public static boolean lineLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		// calculate the direction of the lines
		double uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
		double uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));

		// if uA and uB are between 0-1, lines are colliding
		if(uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {return(true);}
		return(false);
	}
	
	public static double[] whereLineLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		// calculate the direction of the lines
		double uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
		double uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));

		// if uA and uB are between 0-1, lines are colliding
		if(uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {return(new double[]{x1 + (uA * (x2-x1)), y1 + (uA * (y2-y1))});}
		return(new double[]{0, 0});
	}
	
	public static boolean lineRect(double x1, double y1, double x2, double y2, double rx, double ry, double rw, double rh) {
		// check if the line has hit any of the rectangle's sides
		// uses the Line/Line function below
		boolean left =   lineLine(x1,y1,x2,y2, rx,ry,rx, ry+rh);
		boolean right =  lineLine(x1,y1,x2,y2, rx+rw,ry, rx+rw,ry+rh);
		boolean top =    lineLine(x1,y1,x2,y2, rx,ry, rx+rw,ry);
		boolean bottom = lineLine(x1,y1,x2,y2, rx,ry+rh, rx+rw,ry+rh);

		// if ANY of the above are true, the line
		// has hit the rectangle/
		// also checks if both endpoints of the line are contained in the rectangle
		if(
			left ||
			right ||
			top ||
			bottom ||
			x1 > rx && x1 < rx + rw && y1 > ry && y1 < ry + rh &&
			x2 > rx && x2 < rx + rw && y2 > ry && y2 < ry + rh
		) {return(true);}
		return(false);
	}
	
	public static boolean lineRectEdges(double x1, double y1, double x2, double y2, double rx, double ry, double rw, double rh) {
		// check if the line has hit any of the rectangle's sides
		// uses the Line/Line function below
		boolean left =   lineLine(x1,y1,x2,y2, rx,ry,rx, ry+rh);
		boolean right =  lineLine(x1,y1,x2,y2, rx+rw,ry, rx+rw,ry+rh);
		boolean top =    lineLine(x1,y1,x2,y2, rx,ry, rx+rw,ry);
		boolean bottom = lineLine(x1,y1,x2,y2, rx,ry+rh, rx+rw,ry+rh);

		// if ANY of the above are true, the line
		// has hit the rectangle/
		// DOESN'T check if both endpoints of the line are contained in the rectangle
		if(
			left ||
			right ||
			top ||
			bottom
		) {return(true);}
		return(false);
	}
	
	public static boolean[] whichLineRectEdges(double x1, double y1, double x2, double y2, double rx, double ry, double rw, double rh) {
		// check if the line has hit any of the rectangle's sides
		// uses the Line/Line function below
		boolean left =   lineLine(x1,y1,x2,y2, rx,ry,rx, ry+rh);
		boolean right =  lineLine(x1,y1,x2,y2, rx+rw,ry, rx+rw,ry+rh);
		boolean top =    lineLine(x1,y1,x2,y2, rx,ry, rx+rw,ry);
		boolean bottom = lineLine(x1,y1,x2,y2, rx,ry+rh, rx+rw,ry+rh);

		// if ANY of the above are true, the line
		// has hit the rectangle/
		// DOESN'T check if both endpoints of the line are contained in the rectangle
		return(new boolean[]{left, right, top, bottom});
	}
	
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
	
	private static double[] realPopOut(double x, double y, double w, double h, double[/* index */][/* x, y, w, h */] tiles) {
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
				}
			}
		}
		
		if(smallestIndex != -1) {return(offsets[smallestIndex]);}
		
		// if smallest index = -1
		return(new double[]{0, 0}); // returns [x movement, y movement]
	}

	public static double[] angularPopOut(double x, double y, double w, double h, double cx, double cy, double angle, double distance, double[/* index */][/* x, y, w, h */] tiles) {
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
		
		return(realAngularPopOut(x, y, w, h, cx, cy, angle, distance, collisionData));
	}
	public static double[] angularPopOut(double x, double y, double w, double h, double cx, double cy, double angle, double distance, int[/* index */][/* x, y, w, h */] tiles) {
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
		
		return(realAngularPopOut(x, y, w, h, cx, cy, angle, distance, collisionData));
	}
	
	private static double[] realAngularPopOut(double x, double y, double w, double h, double cx, double cy, double angle, double distance, double[/* index */][/* x, y, w, h */] tiles) {
		/*
		
		this function checks to see if the player is colliding with any solids
		if it is, it returns the smallest movement (represented by difference in angle) it can make on the circle to not be colliding with anything, while keeping the original distance
		if it isn't, it simply returns the original position
		
		for future reference, the center of the player is (x + w / 2, y + h / 2)
		this is what the distance uses - the distance from the center of the player to the anchor point
		
		*/
		
		double[/* index = 8 possible movements per tile */][/* x movement, y movement */] offsets = new double[tiles.length * 8][2];
		
		for(int i = 0; i < offsets.length; i += 8) {
			// left (negative? number)
			offsets[i + 0][0] = tiles[(int)Math.floor(i / 8)][0] - (x + w);
			offsets[i + 0][1] = -(y + h / 2 - getCircleLineIntersectionPoint(
					x + w / 2 + (tiles[(int)Math.floor(i / 8)][0] - (x + w)), y + h / 2,
					x + w / 2 + (tiles[(int)Math.floor(i / 8)][0] - (x + w)), y + h / 2 + 1,
					cx, cy, distance
			)[0][1]);
			offsets[i + 1][0] = tiles[(int)Math.floor(i / 8)][0] - (x + w);
			offsets[i + 1][1] = -(y + h / 2 - getCircleLineIntersectionPoint(
					x + w / 2 + (tiles[(int)Math.floor(i / 8)][0] - (x + w)), y + h / 2,
					x + w / 2 + (tiles[(int)Math.floor(i / 8)][0] - (x + w)), y + h / 2 + 1,
					cx, cy, distance
			)[1][1]);
			
			// right (positive? number)
			offsets[i + 2][0] = (tiles[(int)Math.floor(i / 8)][0] + tiles[(int)Math.floor(i / 8)][2]) - x;
			offsets[i + 2][1] = -(y + h / 2 - getCircleLineIntersectionPoint(
					x + w / 2 + ((tiles[(int)Math.floor(i / 8)][0] + tiles[(int)Math.floor(i / 8)][2]) - x), y + h / 2,
					x + w / 2 + ((tiles[(int)Math.floor(i / 8)][0] + tiles[(int)Math.floor(i / 8)][2]) - x), y + h / 2 + 1,
					cx, cy, distance
			)[0][1]);
			offsets[i + 3][0] = (tiles[(int)Math.floor(i / 8)][0] + tiles[(int)Math.floor(i / 8)][2]) - x;
			offsets[i + 3][1] = -(y + h / 2 - getCircleLineIntersectionPoint(
					x + w / 2 + ((tiles[(int)Math.floor(i / 8)][0] + tiles[(int)Math.floor(i / 8)][2]) - x), y + h / 2,
					x + w / 2 + ((tiles[(int)Math.floor(i / 8)][0] + tiles[(int)Math.floor(i / 8)][2]) - x), y + h / 2 + 1,
					cx, cy, distance
			)[1][1]);
			
			// up (negative? number)
			offsets[i + 4][0] = -(x + w / 2 - getCircleLineIntersectionPoint(
					x + w / 2,     y + h / 2 + (tiles[(int)Math.floor(i / 8)][1] - (y + h)),
					x + w / 2 + 1, y + h / 2 + (tiles[(int)Math.floor(i / 8)][1] - (y + h)),
					cx, cy, distance
			)[0][0]);
			offsets[i + 4][1] = tiles[(int)Math.floor(i / 8)][1] - (y + h);
			offsets[i + 5][0] = -(x + w / 2 - getCircleLineIntersectionPoint(
					x + w / 2,     y + h / 2 + (tiles[(int)Math.floor(i / 8)][1] - (y + h)),
					x + w / 2 + 1, y + h / 2 + (tiles[(int)Math.floor(i / 8)][1] - (y + h)),
					cx, cy, distance
			)[1][0]);
			offsets[i + 5][1] = tiles[(int)Math.floor(i / 8)][1] - (y + h);
			
			// down (positive? number)
			offsets[i + 6][0] = -(x + w / 2 - getCircleLineIntersectionPoint(
					x + w / 2,     y + h / 2 + ((tiles[(int)Math.floor(i / 8)][1] + tiles[(int)Math.floor(i / 8)][3]) - y),
					x + w / 2 + 1, y + h / 2 + ((tiles[(int)Math.floor(i / 8)][1] + tiles[(int)Math.floor(i / 8)][3]) - y),
					cx, cy, distance
			)[0][0]);
			offsets[i + 6][1] = (tiles[(int)Math.floor(i / 8)][1] + tiles[(int)Math.floor(i / 8)][3]) - y;
			offsets[i + 7][0] = -(x + w / 2 - getCircleLineIntersectionPoint(
					x + w / 2,     y + h / 2 + ((tiles[(int)Math.floor(i / 8)][1] + tiles[(int)Math.floor(i / 8)][3]) - y),
					x + w / 2 + 1, y + h / 2 + ((tiles[(int)Math.floor(i / 8)][1] + tiles[(int)Math.floor(i / 8)][3]) - y),
					cx, cy, distance
			)[1][0]);
			offsets[i + 7][1] = (tiles[(int)Math.floor(i / 8)][1] + tiles[(int)Math.floor(i / 8)][3]) - y;
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
				}
			}
		}
		
		if(smallestIndex != -1) {return(new double[]{offsets[smallestIndex][0], offsets[smallestIndex][1]});} // change
		
		return(new double[]{0, 0/*, 0*/}); // no change
	}
	
	public static double[] getClosestCircleLineIntersectionPoint(double x1, double y1, double x2, double y2, double cx, double cy, double radius) { // closest to x1
		double[] pointA = new double[]{x1, y1};
		double[] pointB = new double[]{x2, y2};
		double[] center = new double[]{cx, cy};
		
		double temp[][] = realGetCircleLineIntersectionPoint(pointA, pointB, center, radius);
		
		double dx1 = Math.abs(x1 - temp[0][0]);
		double dy1 = Math.abs(y1 - temp[0][1]);
		double dx2 = Math.abs(x1 - temp[1][0]);
		double dy2 = Math.abs(y1 - temp[1][1]);
		
		if(Math.sqrt((dx2 * dx2) + (dy2 * dy2)) < Math.sqrt((dx1 * dx1) + (dy1 * dy1))) {return(temp[1]);}
		
		return(temp[0]);
	}
	
	public static double[][] getCircleLineIntersectionPoint(double x1, double y1, double x2, double y2, double cx, double cy, double radius) {
		double[] pointA = new double[]{x1, y1};
		double[] pointB = new double[]{x2, y2};
		double[] center = new double[]{cx, cy};
		
		return(realGetCircleLineIntersectionPoint(pointA, pointB, center, radius));
	}
	
	private static double[][] realGetCircleLineIntersectionPoint(double[] pointA, double[] pointB, double[] center, double radius) {
        double baX = pointB[0] - pointA[0];
        double baY = pointB[1] - pointA[1];
        double caX = center[0] - pointA[0];
        double caY = center[1] - pointA[1];

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if(disc < 0) {return(new double[][]{{0, 0}, {0, 0}});}
        // if disc == 0 ... dealt with later
        
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        double[] p1 = new double[]{pointA[0] - baX * abScalingFactor1, pointA[1] - baY * abScalingFactor1};
        if(disc == 0) {return(new double [][]{p1, p1});} // abScalingFactor1 == abScalingFactor2
        
        double[] p2 = new double[]{pointA[0] - baX * abScalingFactor2, pointA[1] - baY * abScalingFactor2};
        return(new double [][]{p1, p2});
    }
}
