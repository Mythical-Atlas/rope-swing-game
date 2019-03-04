package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Animation {
	private int timer;
	private int frame;
	private int speed;
	private int repeatFrame;
	private boolean finished; // only for first cycle
	private BufferedImage[] frames;
	
	public Animation(BufferedImage[] frames, int speed, int repeatFrame) {
		this.frames = frames;
		this.speed = speed;
		this.repeatFrame = repeatFrame;
	}
	
	public void reset() {
		timer = 0;
		frame = 0;
		finished = false;
	}
	
	public boolean isFinished() {return(finished);}
	
	public void changeSpeed(int speed) {this.speed = speed;}
	
	public void update() {
		timer++;
		if(timer >= speed) {
			timer = 0;
			frame++;
			
			if(frame >= frames.length) {
				if(repeatFrame != -1) {frame = repeatFrame;}
				//else {frame = frames.length - 1;}
				
				finished = true;
			}
		}
		
		/*System.out.println("timer = " + timer);
		System.out.println("frame = " + frame);
		System.out.println("length = " + frames.length);*/
	}
	
	public void draw(Graphics2D graphics, int x, int y) {graphics.drawImage (frames[frame], x, y, null);}
	public void draw(Graphics2D graphics, double x, double y) {graphics.drawImage(frames[frame], (int)x, (int)y, null);}
	
	public void draw(Graphics2D graphics, int x, int y, int xScale, int yScale) {graphics.drawImage (frames[frame], x, y, frames[frame].getWidth() * xScale, frames[frame].getHeight() * yScale, null);}
	public void draw(Graphics2D graphics, double x, double y, int xScale, int yScale) {graphics.drawImage(frames[frame], (int)x, (int)y, frames[frame].getWidth() * xScale, frames[frame].getHeight() * yScale, null);}
}
