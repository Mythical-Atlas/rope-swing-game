package states;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

public abstract class State {
	public int switchStates = -1;
	
	public abstract void update(JFrame frame);
	public abstract void draw(Graphics2D graphics);
	
	public abstract void keyPressed(int key);
	public abstract void keyReleased(int key);
	
	public abstract void mouseClicked(MouseEvent mouse);
	public abstract void mouseEntered(MouseEvent mouse);
	public abstract void mouseExited(MouseEvent mouse);
	public abstract void mousePressed(MouseEvent mouse);
	public abstract void mouseReleased(MouseEvent mouse);
}