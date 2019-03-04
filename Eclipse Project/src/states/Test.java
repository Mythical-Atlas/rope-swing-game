package states;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.JFrame;

import main.Main;
import main.MapReader;
import objects.Player;
import objects.Tile;

public class Test extends State {
	//Player player;
	
	Tile[] tiles;
	
	int mapWidth;
	int mapHeight;
	int tileWidth;
	int tileHeight;
	
	int error;
	String errorType;
	
	// options
	boolean showControls = true;
	
	public Test() {
		int[] tileMapInfo = null;
		
		try {
			tiles = MapReader.readTileMap(getClass().getClassLoader().getResourceAsStream("testTileMap.json"));
			tileMapInfo =  MapReader.readTileMapInfo(getClass().getClassLoader().getResourceAsStream("testTileMap.json"));
		}
		catch(Exception e) {
			e.printStackTrace();
			
			error = 1;
			errorType = e.getClass().getCanonicalName();
		}
		
		if(error == 0) {
			mapWidth = tileMapInfo[0];
			mapHeight = tileMapInfo[1];
			tileWidth = tileMapInfo[2];
			tileHeight = tileMapInfo[3];
			
			new Player(2 * tileWidth, 58 * tileHeight, mapWidth * tileWidth, mapHeight * tileHeight);
		}
	}
	
	public void update(JFrame frame) {
		if(error == 0) {
			int[][] collisionData = new int[tiles.length][4];
			
			for(int i = 0; i < tiles.length; i++) {collisionData[i] = new int[]{tiles[i].x, tiles[i].y, tiles[i].w, tiles[i].h};}
			
			Player.update(collisionData, frame);
		}
	}
	public void draw(Graphics2D graphics) {
		if(error == 0) {
			int xOffset = Player.xOffset;
			int yOffset = Player.yOffset;
			int fontHeight = graphics.getFontMetrics().getHeight();
			
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
			
			for(int i = 0; i < tiles.length; i++) {tiles[i].draw(Player.x, Player.y, Player.w, Player.h, graphics, xOffset, yOffset);}
			
			Player.draw(graphics);
			
			graphics.setColor(Color.DARK_GRAY);
			graphics.drawString("Programmed by Ben Correll", fontHeight / 2, Main.HEIGHT);
			
			// display info + options
			graphics.setColor(Color.WHITE);
			
			if(showControls) {
				graphics.drawString("Controls:", fontHeight / 2, fontHeight * 1);
				graphics.drawString("F1 = toggle controls overlay", fontHeight / 2, fontHeight * 2);
				graphics.drawString("A/D = move character (works while swinging)", fontHeight / 2, fontHeight * 3);
				graphics.drawString("R = reset", fontHeight / 2, fontHeight * 4);
				graphics.drawString("Space = jump (works while swinging) + walljump (works while swinging)", fontHeight / 2, fontHeight * 5);
				graphics.drawString("Left Mouse Button = swing", fontHeight / 2, fontHeight * 6);
				graphics.drawString("Right Mouse Button = yank (on ground or sliding on wall) + shorten rope distance while swinging (release to go back to max distance)", fontHeight / 2, fontHeight * 7);
			}
			
			/*graphics.drawString("Player World Position:", 0,              		fontHeight *  1);
			graphics.drawString("X = " + round(player.x, 2), 0,           		fontHeight *  2);
			graphics.drawString("Y = " + round(player.y, 2), 0,           		fontHeight *  3);
			graphics.drawString("Player Screen Position:", 0,             		fontHeight *  4);
			graphics.drawString("X = " + round(player.x + xOffset, 2), 0, 		fontHeight *  5);
			graphics.drawString("Y = " + round(player.y + yOffset, 2), 0, 		fontHeight *  6);
			
			graphics.drawString("Player Speed:", 0,                        		fontHeight *  8);
			graphics.drawString("X = " + round(player.dx, 2), 0,          		fontHeight *  9);
			graphics.drawString("Y = " + round(player.dy, 2), 0,          		fontHeight * 10);
			graphics.drawString("A = " + round(player.da, 2), 0,          		fontHeight * 11);
			
			graphics.drawString("Mouse Screen Position:", 0,                    fontHeight * 12);
			graphics.drawString("X = " + round(player.xMouse + xOffset, 2), 0,          	fontHeight * 13);
			graphics.drawString("Y = " + round(player.yMouse + xOffset, 2), 0,          	fontHeight * 14);*/
		}
		else {
			System.err.println("ERROR");
			
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
			
			graphics.setColor(Color.BLACK);
			graphics.drawString(errorType, 0, graphics.getFontMetrics().getHeight());
		}
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public void keyPressed(int key) {
		Player.keyPressed(key);
		if(key == KeyEvent.VK_F1) {showControls = !showControls;}
	}
	public void keyReleased(int key) {Player.keyReleased(key);}

	public void mouseClicked(MouseEvent mouse) {Player.mouseClicked(mouse);}
	public void mouseEntered(MouseEvent mouse) {Player.mouseEntered(mouse);}
	public void mouseExited(MouseEvent mouse) {Player.mouseExited(mouse);}
	public void mousePressed(MouseEvent mouse) {Player.mousePressed(mouse);}
	public void mouseReleased(MouseEvent mouse) {Player.mouseReleased(mouse);}
}