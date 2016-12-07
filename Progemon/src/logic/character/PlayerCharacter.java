package logic.character;

import java.io.File;

import graphic.Animation;
import graphic.DrawingUtility;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import logic.player.HumanPlayer;
import manager.WorldManager;
import utility.Pokedex;

public class PlayerCharacter extends Animation{
	
	private static float x, y;
	private static int blockX, blockY;
	private static int direction;
	private static int frameLimit = 2;
	private static int legState = 0;
	private static final int FAST_DELAY = 2, MEDIAM_DELAY = 5, SLOW_DELAY = 8, VERYSLOW_DELAY = 11;
	private static boolean moving = false, walking = false, turning = false, stucking = false;
	private static final String DEFAULT_PATH = "load\\img\\player\\Boy.png";
	
	private static HumanPlayer me = new HumanPlayer("Mhee", Color.BROWN);
	
	public PlayerCharacter() {
		super(DrawingUtility.resize(new Image(new File(DEFAULT_PATH).toURI().toString()), 2), 2);
		setFrameDelay(3);
		
		Pokemon charlizard = Pokedex.getPokemon("Charlizard");
		charlizard.setLevel(40);

		Pokemon caterpie = Pokedex.getPokemon("Caterpie");
		caterpie.setLevel(5);
		
		me.addPokemon(charlizard);
		me.addPokemon(caterpie);
	}
	
	@Override
	public void play() {
		// TODO Auto-generated method stub
		frameDelay = FAST_DELAY;
		frameLimit = 2;
		stucking = false;
		super.play();
	}

	public void walk() {
		int x = blockX + (direction - 2) * (direction % 2);
		int y = blockY + (direction - 1) * (direction % 2 - 1);
		System.out.println("Player walk --> x : " + x + ", y : " + y);
		play();
		walking = true;
		moving = true;
		WorldManager.getObjectAt(x, y).entered();
		WorldManager.getObjectAt(blockX, blockY).exit();
	}
	
	public void turn(int newDirection) {
		play();
		turning = true;
		direction = newDirection;
	}
	
	public void stuck() {
		play();
		stucking = true;
		moving = true;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(!playing) {
			return;
		}
		if(walking) {
			walkUpdate();
		}
		else if(turning) {
			turnUpdate();
		}
		else if(stucking) {
			stuckUpdate();
		}
	}
	
	public void walkUpdate() {
		switch (direction) {
		case 0:
			y += 32f / (VERYSLOW_DELAY + 1);
			break;
		case 1:
			x -= 32f / (VERYSLOW_DELAY + 1);
			break;
		case 2:
			y -= 32f / (VERYSLOW_DELAY + 1);
			break;
		case 3:
			x += 32f / (VERYSLOW_DELAY + 1);
			break;
		}
		if(frameDelay > delayCounter) {
			delayCounter++;
			return;
		}
		else if(frameLimit != 0) {
			currentFrame++;
			frameLimit--;
			currentFrame %= frameNumber;
			if(frameDelay == FAST_DELAY) {
				frameDelay = MEDIAM_DELAY;
			}
			else {
				frameDelay = FAST_DELAY;
			}
		}
		else{
			frameLimit = 2;
			legState++;
			legState %= 2;
			switch (direction) {
			case 0:
				blockY += 1;
				break;
			case 1:
				blockX -= 1;
				break;
			case 2:
				blockY -= 1;
				break;
			case 3:
				blockX += 1;
				break;
			}
			x = blockX * 32;
			y = blockY * 32;
			walking = false;
			pause();
			WorldManager.getObjectAt(blockX, blockY).step();
		}
		delayCounter = 0;
	}
	
	public void stuckUpdate() {
		if(frameDelay > delayCounter) {
			delayCounter++;
			return;
		}
		else if(frameLimit != 0) {
			currentFrame++;
			currentFrame %= frameNumber;
			frameLimit--;
			if(frameDelay == FAST_DELAY) {
				frameDelay = VERYSLOW_DELAY;
			}
			else if(frameDelay == VERYSLOW_DELAY) {
				frameDelay = SLOW_DELAY;
			}
		}
		else{
			frameLimit = 2;
			legState++;
			legState %= 2;
			stucking = false;
			pause();
		}
		delayCounter = 0;
	}
	
	public void turnUpdate() {
		if(FAST_DELAY > delayCounter) {
			delayCounter++;
			return;
		}
		else if(frameLimit != 0) {
			currentFrame++;
			currentFrame %= frameNumber;
			frameLimit--;
		}
		else {
			frameLimit = 2;
			legState++;
			legState %= 2;
			turning = false;
			pause();
			WorldManager.getObjectAt(blockX, blockY).step();
		}
		delayCounter = 0;
	}
	
	@Override
	public Image getCurrentImage() {
		WritableImage wimg = new WritableImage(image.getPixelReader(), (legState * 2 + currentFrame) * 32, direction * 44, 32, 44);
		return wimg;
	}
	
	public static double getX() {
		return x;
	}
	
	public static double getY() {
		return y;
	}
	
	public static void setX(float x) {
		PlayerCharacter.x = x;
	}
	
	public static void setY(float y) {
		PlayerCharacter.y = y;
	}
	
	public static int getBlockX() {
		return blockX;
	}
	
	public static int getBlockY() {
		return blockY;
	}
	
	public static void setBlockX(int blockX) {
		PlayerCharacter.blockX = blockX;
	}
	
	public static void setBlockY(int blockY) {
		PlayerCharacter.blockY = blockY;
	}
	
	public static int getDirection() {
		return direction;
	}
	
	public static void setDirection(int direction) {
		PlayerCharacter.direction = direction;
	}
	
	public static boolean isMoving() {
		return moving;
	}
	
	public static void setMoving(boolean moving) {
		PlayerCharacter.moving = moving;
	}
	
	public static boolean isWalking() {
		return walking;
	}
	
	public static boolean isStucking() {
		return stucking;
	}
	
	@Override
	public int getDepth() {
		// TODO Auto-generated method stub
		return (int) y;
	}
	
	@Override
	public void draw() {
		// TODO Auto-generated method stub
		DrawingUtility.drawPlayer(this);
	}

	public static final HumanPlayer getMe() {
		return me;
	}

}
