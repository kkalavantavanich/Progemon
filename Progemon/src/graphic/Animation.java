package graphic;

import java.io.File;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import logic.character.ActiveSkill;

public abstract class Animation implements IRenderable{
	
	/** Image used for animation. Must be consisted of 5 frames per row */
	private Image image;
	private int frameNumber, frameDelay, currentFrame, delayCounter;
	private boolean playing;
	
	public Animation() {
		// TODO Auto-generated constructor stub
		frameDelay = 1;
	}
	
	public Animation(Image image, int frameNumber) {
		// TODO Auto-generated constructor stub
		this.image = image;
		this.frameNumber = frameNumber;
		frameDelay = 1;
	}
	
	public void play() {
		playing = true;
		delayCounter = 0;
		currentFrame = 0;
		AnimationHolder.addPlayingAnimations(this);
	}
	
	public void update() {
		if(!playing) {
			return;
		}
		if(frameDelay > delayCounter) {
			delayCounter++;
		}
		else if(frameNumber - 1 > currentFrame) {
			currentFrame++;
			delayCounter = 0;
		}
		else{
			stop();
		}
	}
	
	public void stop() {
		playing = false;
		delayCounter = 0;
		currentFrame = 0;
		AnimationHolder.removePlayingAnimations(this);
	}
	
	public Image getCurrentImage() {
		WritableImage wimg = new WritableImage(image.getPixelReader(), currentFrame % 5 * 160, (int) (Math.floor(currentFrame / 5) * 80), 160, 80);
		return wimg;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public void loadImage(String filePath) {
		/*if(name.equals("Flamethrower")) {
		File file = new File("load/img/skill/" + name + "/all.png");
		Image image = new Image(file.toURI().toString());
		animation = new Animation(image, 16);
		}*/
		File file = new File(filePath);
		this.image = new Image(file.toURI().toString());
		frameNumber = 16;
	}

}