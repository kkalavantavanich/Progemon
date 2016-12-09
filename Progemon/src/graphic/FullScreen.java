package graphic;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Screen;

public class FullScreen extends Canvas {
	
	public static final int SCREEN_WIDTH = (int) Screen.getPrimary().getBounds().getWidth();
	public static final int SCREEN_HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();
	public static final int WIDTH = (int) ((480 * Screen.getPrimary().getBounds().getHeight()) / 384);
	public static final int HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();
	public static WritableImage wimg = new WritableImage(GameScreen.WIDTH, GameScreen.HEIGHT);
	public static Image image; 
	private static GraphicsContext gc;
	
	public FullScreen() {
		// TODO Auto-generated constructor stub
		super(SCREEN_WIDTH, SCREEN_HEIGHT);
		gc = getGraphicsContext2D();
	}
	
	public static void repaint() {
		gc.clearRect(0, 0, WIDTH, HEIGHT);
		System.gc();
		image = GameStage.getCanvas().snapshot(null, wimg);
		gc.drawImage(image, (SCREEN_WIDTH - WIDTH) / 2, 0, WIDTH, HEIGHT);
	}

}