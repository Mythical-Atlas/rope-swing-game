package objects;

import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.JFrame;

import main.Collision;

public class MiscFunctions {
	public static void scrollScreen() {
		int screenWidth = 1280;
		int screenHeight = 720;
		int worldWidth = 1280 * 2;
		int worldHeight = 960 * 2;
		
		double xReference = Player.x + Player.w / 2;
		double yReference = Player.y + Player.h / 2;
		
		// scrolls screen if Player is past certain points
		     if(xReference + Player.xOffset > screenWidth / 8 * 5) {Player.xOffset = (int)(screenWidth / 8 * 5 - xReference);} // move screen right
		else if(xReference + Player.xOffset < screenWidth / 8 * 3) {Player.xOffset = (int)(screenWidth / 8 * 3 - xReference);} // move screen left
		     if(yReference + Player.yOffset > screenHeight / 8 * 5) {Player.yOffset = (int)(screenHeight / 8 * 5 - yReference);} // move screen down
		else if(yReference + Player.yOffset < screenHeight / 8 * 3) {Player.yOffset = (int)(screenHeight / 8 * 3 - yReference);} // move screen up
		
		if(Player.xOffset > 0) {Player.xOffset = 0;}
		if(Player.xOffset < -(worldWidth - screenWidth)) {Player.xOffset = -(worldWidth - screenWidth);}
		if(Player.yOffset > 0) {Player.yOffset = 0;}
		if(Player.yOffset < -(worldHeight - screenHeight)) {Player.yOffset = -(worldHeight - screenHeight);}
	}

	public static void getMousePos(int[][] tiles, JFrame frame) {
		Point mousePos = MouseInfo.getPointerInfo().getLocation(); // gets mouse pos on monitor
		
		// translate above position to be mouse position in frame of window
		Player.xMouse = mousePos.x - frame.getLocationOnScreen().x - 3 - Player.xOffset;
		Player.yMouse = mousePos.y - frame.getLocationOnScreen().y - 26 - Player.yOffset;
		
		double[] oofNotReally = MiscFunctions.getMouseRay(tiles); // get mouse "ray" end point from mouse pos
		
		Player.xTempMouse = Player.xMouse;
		Player.yTempMouse = Player.yMouse;
		
		Player.xMouseRay = Player.xTempMouse;
		Player.yMouseRay = Player.xTempMouse;
		
		// mouse line = line between center of player and mouse
		
		Player.canHook = false;
		
		// INNACCURATE (not sure why yet - sometimes lags behind mouse pos; sometimes completely wrong spot, although rarely)
		trimMouseLine(tiles); // trims mouse line to closest tile edge on line
	}
	
	public static double[] getMouseRay(int[][] tiles) {
		double xTempMouse = Player.xMouse;
		double yTempMouse = Player.yMouse;
		
		double xMouseDist = ((Player.x + Player.w / 2) - Player.xMouse);
		double yMouseDist = ((Player.y + Player.h / 2) - Player.yMouse);
		
		//double xLength = 1280 / 2;
		//double yLength = 720 / 2;
		
		double xTempTempDistLeft = 1280 - (Player.x + Player.w / 2) + Player.xOffset;
		double xTempTempDistRight = -((Player.x + Player.w / 2) + Player.xOffset - 1280);
		double yTempTempDistUp = 720 - (Player.y + Player.h / 2) + Player.yOffset;
		double yTempTempDistDown = -((Player.y + Player.h / 2) + Player.yOffset - 720);
		
		double tempAngle = Math.atan(yMouseDist / xMouseDist); // INACCURATE (not sure why yet)
		double screenCorner = Math.sqrt(1280 * 1280 + 720 * 720);
		
		if(((Player.x + Player.w / 2) - Player.xMouse) > 0) {screenCorner *= -1;}
		
		// sets default mouse ray end to be farthest possible distance on screen (corner to corner)
		xTempMouse = Math.cos(tempAngle) * screenCorner + Player.x;
		yTempMouse = Math.sin(tempAngle) * screenCorner + Player.y;
		
		double leftScreen = Player.x + Player.w / 2 - xTempTempDistLeft;
		double rightScreen = Player.x + Player.w / 2 - xTempTempDistRight;
		double topScreen = Player.y + Player.h / 2 - yTempTempDistUp;
		double bottomScreen = Player.y + Player.h / 2 - yTempTempDistDown;
		
		double[] screenDist = new double[]{Player.xMouse, Player.yMouse};
		
		// clips mouse line to end of screen
		if(Collision.lineLine(Player.x + Player.w / 2, Player.y + Player.h / 2, xTempMouse, yTempMouse, leftScreen, topScreen, leftScreen, bottomScreen)) {screenDist = Collision.whereLineLine(Player.x + Player.w / 2, Player.y + Player.h / 2, xTempMouse, yTempMouse, leftScreen, topScreen, leftScreen, bottomScreen);} // left
		else if(Collision.lineLine(Player.x + Player.w / 2, Player.y + Player.h / 2, xTempMouse, yTempMouse, rightScreen, topScreen, rightScreen, bottomScreen)) {screenDist = Collision.whereLineLine(Player.x + Player.w / 2, Player.y + Player.h / 2, xTempMouse, yTempMouse, rightScreen, topScreen, rightScreen, bottomScreen);} // right
		else if(Collision.lineLine(Player.x + Player.w / 2, Player.y + Player.h / 2, xTempMouse, yTempMouse, leftScreen, topScreen, rightScreen, topScreen)) {screenDist = Collision.whereLineLine(Player.x + Player.w / 2, Player.y + Player.h / 2, xTempMouse, yTempMouse, leftScreen, topScreen, rightScreen, topScreen);} // top
		else if(Collision.lineLine(Player.x + Player.w / 2, Player.y + Player.h / 2, xTempMouse, yTempMouse, leftScreen, bottomScreen, rightScreen, bottomScreen)) {screenDist = Collision.whereLineLine(Player.x + Player.w / 2, Player.y + Player.h / 2, xTempMouse, yTempMouse, leftScreen, bottomScreen, rightScreen, bottomScreen);} // down
		
		xMouseDist = ((Player.x + Player.w / 2) - screenDist[0]);
		yMouseDist = ((Player.y + Player.h / 2) - screenDist[1]);
		
		screenCorner = Math.sqrt(xMouseDist * xMouseDist + yMouseDist * yMouseDist);
		if(((Player.x + Player.w / 2) - Player.xMouse) > 0) {screenCorner *= -1;}
		
		xTempMouse = Math.cos(tempAngle) * screenCorner + Player.x;
		yTempMouse = Math.sin(tempAngle) * screenCorner + Player.y;
		
		// check if mouse ray goes past screen
		// if yes, cut it to edge of screen
		/*if(xTempMouse - xOffset < 0) {xLength = ((x + w / 2) - xTempMouse);}
		if(yTempMouse - yOffset < 0) {xTempMouse = yOffset;}
		if(xTempMouse > 1280 + xOffset) {xTempMouse = 1280 + xOffset;}
		if(yTempMouse > 720 + yOffset) {yTempMouse = 720 + yOffset;}*/
		
		return(new double[]{xTempMouse,	yTempMouse});
	}
	
	public static void trimMouseLine(int[][] tiles) {
		// check if mouse line is colliding with any tiles
		int stopped = 0;
		for(int i = 0; i < tiles.length; i++) {if(Collision.lineRect(Player.x + Player.w / 2, Player.y + Player.h / 2, Player.xMouse, Player.yMouse, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {stopped++;}}
					
		// if so, trim the line so that it goes from the center of the player to the closest tile on the line
		if(stopped > 0) {
			int[][] collidingTiles = new int[stopped][4];
			
			Player.canHook = true;
			
			// get list of tiles colliding with mouse line
			int index = 0;
			for(int i = 0; i < tiles.length; i++) {
				if(Collision.lineRectEdges(Player.x + Player.w / 2, Player.y + Player.h / 2, Player.xMouse, Player.yMouse, tiles[i][0], tiles[i][1], tiles[i][2], tiles[i][3])) {
					collidingTiles[index] = tiles[i];
					index++;
				}
			}
			
			// check which sides of the colliding tiles pass through mouse line
			int numPossibilities = collidingTiles.length * 4;
			double[][] possibilities = new double[numPossibilities][2];
			boolean[] works = new boolean[numPossibilities];
			
			for(int i = 0; i < collidingTiles.length; i++) {
				boolean[] tempBools = Collision.whichLineRectEdges(Player.x + Player.w / 2, Player.y + Player.h / 2, Player.xTempMouse, Player.yTempMouse, collidingTiles[i][0], collidingTiles[i][1], collidingTiles[i][2], collidingTiles[i][3]);
			
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
					
					possibilities[o] = Collision.whereLineLine(Player.x + Player.w / 2, Player.y + Player.h / 2, Player.xTempMouse, Player.yTempMouse, x1, y1, x2, y2);}
			}
			
			// find intersecting point closest to player
			// intersecting point refering to point of intersection between mouse line and a given side of a tile
			double smallestDistance = 0;
			int smallestIndex = -1;
			for(int i = 0; i < possibilities.length; i++) {
				if(works[i]) {
					double xDist = Math.abs((Player.x + Player.w / 2) - possibilities[i][0]);
					double yDist = Math.abs((Player.y + Player.h / 2) - possibilities[i][1]);
					double tempDistance = Math.sqrt(xDist * xDist + yDist * yDist);
					
					if(tempDistance < smallestDistance || smallestIndex == -1) {
						smallestDistance = tempDistance;
						smallestIndex = i;
					}
				}
			}
			
			// set mouseRay position to be closest intersecting point
			if(smallestIndex != -1) {
				Player.xMouseRay = possibilities[smallestIndex][0];
				Player.yMouseRay = possibilities[smallestIndex][1];
			}
		}
	}

	public static void reset() {
		Player.reset = false;
		
		Player.x = Player.ox;
		Player.y = Player.oy;
		Player.dx = 0;
		Player.dy = 0;
		Player.da = 0;
		Player.leftMouse = false;
		Player.swinging = false;
		Player.canHook = false;
	}
}
