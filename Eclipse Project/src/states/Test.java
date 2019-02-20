package states;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import objects.Player;
import objects.TestBlip;
import objects.Tile;

public class Test extends State {
	Player player;
	
	Tile[] tiles;
	
	public Test() {
		player = new Player(320 - 16 + 100, 240 + 100 - 16);
		
		tiles = new Tile[26];
		
		tiles[0] = new Tile(3 * 32, 5 * 32, 32, 32);
		tiles[1] = new Tile(4 * 32, 5 * 32, 32, 32);
		tiles[2] = new Tile(5 * 32, 5 * 32, 32, 32);
		tiles[3] = new Tile(6 * 32, 5 * 32, 32, 32);
		tiles[4] = new Tile(7 * 32, 5 * 32, 32, 32);
		
		tiles[5] = new Tile(10 * 32, 9 * 32, 32, 32);
		tiles[6] = new Tile(11 * 32, 9 * 32, 32, 32);
		tiles[7] = new Tile(12 * 32, 9 * 32, 32, 32);
		tiles[8] = new Tile(13 * 32, 9 * 32, 32, 32);
		tiles[9] = new Tile(14 * 32, 9 * 32, 32, 32);
		tiles[10] = new Tile(15 * 32, 9 * 32, 32, 32);
		
		tiles[10] = new Tile(2 * 32, 12 * 32, 32, 32);
		tiles[11] = new Tile(3 * 32, 12 * 32, 32, 32);
		tiles[12] = new Tile(4 * 32, 12 * 32, 32, 32);
		tiles[13] = new Tile(5 * 32, 12 * 32, 32, 32);
		tiles[14] = new Tile(6 * 32, 12 * 32, 32, 32);
		tiles[15] = new Tile(7 * 32, 12 * 32, 32, 32);
		tiles[16] = new Tile(8 * 32, 12 * 32, 32, 32);
		tiles[17] = new Tile(9 * 32, 12 * 32, 32, 32);
		tiles[18] = new Tile(10 * 32, 12 * 32, 32, 32);
		tiles[19] = new Tile(11 * 32, 12 * 32, 32, 32);
		tiles[20] = new Tile(12 * 32, 12 * 32, 32, 32);
		tiles[21] = new Tile(13 * 32, 12 * 32, 32, 32);
		tiles[22] = new Tile(14 * 32, 12 * 32, 32, 32);
		tiles[23] = new Tile(15 * 32, 12 * 32, 32, 32);
		tiles[24] = new Tile(16 * 32, 12 * 32, 32, 32);
		tiles[25] = new Tile(17 * 32, 12 * 32, 32, 32);
	}
	
	public void update(JFrame frame) {
		int[][] collisionData = new int[tiles.length][4];
		
		for(int i = 0; i < tiles.length; i++) {collisionData[i] = new int[]{tiles[i].x, tiles[i].y, tiles[i].w, tiles[i].h};}
		
		player.update(collisionData, frame);
	}
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, 640, 480); // HARDCODED SCREEN SIZE
		
		graphics.setColor(Color.DARK_GRAY);
		graphics.drawString("Programmed by Ben Correll", 0, 480);
		
		for(int i = 0; i < tiles.length; i++) {tiles[i].draw(player.x, player.y, player.w, player.h, graphics);}
		
		player.draw(graphics);
	}
	
	public void keyPressed(int key) {player.keyPressed(key);}
	public void keyReleased(int key) {player.keyReleased(key);}

	public void mouseClicked(MouseEvent mouse) {player.mouseClicked(mouse);}
	public void mouseEntered(MouseEvent mouse) {player.mouseEntered(mouse);}
	public void mouseExited(MouseEvent mouse) {player.mouseExited(mouse);}
	public void mousePressed(MouseEvent mouse) {player.mousePressed(mouse);}
	public void mouseReleased(MouseEvent mouse) {player.mouseReleased(mouse);}
}