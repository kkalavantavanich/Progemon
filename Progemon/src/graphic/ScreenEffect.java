package graphic;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ScreenEffect extends Animation {
	
	public ScreenEffect(int frameNumber, int frameDelay, boolean loop, boolean autostop) {
		// TODO Auto-generated constructor stub
		super(frameNumber, frameDelay, loop, autostop);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		DrawingUtility.drawScreenEffect(this);
	}

	@Override
	public int getDepth() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}
	
	@Override
	public void loadImage(String filePath) {
		// TODO Auto-generated method stub
		File file = new File(filePath);
		image = new Image(file.toURI().toString());
		int width;
		if (frameNumber < 5) {
			width = (int) (image.getWidth() / frameNumber);
		}
		else{
			width = (int) (image.getWidth() / 5);
		}
		int height = (int) (image.getHeight() / Math.ceil(frameNumber / 5.0));
		for (int i = 0; i < frameNumber; i++) {
			Image img = new WritableImage(image.getPixelReader(), i % 5 * width, (int) (Math.floor(i / 5) * height), width, height);
			images.add(DrawingUtility.resize(img, 2));
		}
	}

}