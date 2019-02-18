package states;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import objects.Player;
import objects.TestBlip;
import objects.Tile;

public class Test extends State {
	Player player;
	
	Tile[] tiles;
	
	public Test() {
		player = new Player(320 - 16 + 100, 240 + 100 - 16);
		
		tiles = new Tile[9];
		
		tiles[0] = new Tile(3 * 32, 3 * 32, 32, 32); tiles[1] = new Tile(4 * 32, 3 * 32, 32, 32); tiles[2] = new Tile(5 * 32, 3 * 32, 32, 32);
		tiles[3] = new Tile(3 * 32, 4 * 32, 32, 32); tiles[4] = new Tile(4 * 32, 4 * 32, 32, 32); tiles[5] = new Tile(5 * 32, 4 * 32, 32, 32);
		tiles[6] = new Tile(3 * 32, 5 * 32, 32, 32); tiles[7] = new Tile(4 * 32, 5 * 32, 32, 32); tiles[8] = new Tile(5 * 32, 5 * 32, 32, 32);
	}
	
	public void update() {
		player.update(
			new int[][]{
				{tiles[0].x, tiles[0].y, tiles[0].w, tiles[0].h},
				{tiles[1].x, tiles[1].y, tiles[1].w, tiles[1].h},
				{tiles[2].x, tiles[2].y, tiles[2].w, tiles[2].h},
				{tiles[3].x, tiles[3].y, tiles[3].w, tiles[3].h},
				{tiles[4].x, tiles[4].y, tiles[4].w, tiles[4].h},
				{tiles[5].x, tiles[5].y, tiles[5].w, tiles[5].h},
				{tiles[6].x, tiles[6].y, tiles[6].w, tiles[6].h},
				{tiles[7].x, tiles[7].y, tiles[7].w, tiles[7].h},
				{tiles[8].x, tiles[8].y, tiles[8].w, tiles[8].h},
			}
		);
	}
	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, 640, 480); // HARDCODED SCREEN SIZE
		
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