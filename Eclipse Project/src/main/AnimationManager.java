package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AnimationManager {
	private Animation[] animations;
	private int index;
	
	public AnimationManager(String path, String object, String[] names, int[] speeds, int repeatFrames[]) {
		BufferedImage[] tempImages;
		int length = 0;
		BufferedImage tempFile;
		
		index = 0;
		animations = new Animation[names.length];
		
		for(int i = 0; i < names.length; i++) {
			length = 0;
			
			while(true) {
				try {tempFile = ImageIO.read(getClass().getClassLoader().getResourceAsStream(path + object + " " + names[i] + " " + length + ".png"));}
				catch(Exception e) {break;}
				
				length++;
			}
			
			tempImages = new BufferedImage[length];
			
			try {for(int o = 0; o < length; o++) {tempImages[o] = ImageIO.read(getClass().getClassLoader().getResourceAsStream(path + object + " " + names[i] + " " + o + ".png"));}}
			catch(IOException e) {e.printStackTrace();}
			
			animations[i] = new Animation(tempImages, speeds[i], repeatFrames[i]);
		}
	}
	
	public void changeAnimation(int index) {
		this.index = index;
		
		for(int i = 0; i < animations.length; i++) {animations[i].reset();}
	}
	
	public void changeAnimationSpeed(int index, int speed) {animations[index].changeSpeed(speed);}
	
	public boolean isAnimationFinished() {return(animations[index].isFinished());}
	
	public void changeAnimationWhenFinished(int index) {if(animations[this.index].isFinished()) {changeAnimation(index);}}
	
	public int getIndex() {return(index);}
	
	public void update() {animations[index].update();}
	
	public void draw(Graphics2D graphics, int x, int y) {animations[index].draw(graphics, x, y);}
	public void draw(Graphics2D graphics, double x, double y) {animations[index].draw(graphics, x, y);}
	
	public void draw(Graphics2D graphics, int x, int y, int xScale, int yScale) {animations[index].draw(graphics, x, y, xScale, yScale);}
	public void draw(Graphics2D graphics, double x, double y, int xScale, int yScale) {animations[index].draw(graphics, x, y, xScale, yScale);}
}
