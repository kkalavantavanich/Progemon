package graphic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import logic.terrain.FightMap;
import logic.terrain.FightTerrain;

public class DrawingUtility {
	
	private static ArrayList<IRenderable> objectOnScreen = new ArrayList<IRenderable>();
	private static Graphics2D g2d = (Graphics2D) Frame.getGraphicComponent().getGraphics();
	
	public static ArrayList<IRenderable> getObjectOnScreen() {
		return objectOnScreen;
	}
	
	public static void addObject(IRenderable object) {
		objectOnScreen.add(object);
	}
	
	public static void drawFightMap(FightMap fightMap){
		for (FightTerrain[] fightTerrains : fightMap.getMap()) {
			for (FightTerrain fightTerrain : fightTerrains) {
				fightTerrain.draw();
			}
		}
	}
	
	public static void drawFightTerrain(FightTerrain fightTerrain){
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(fightTerrain.getType().getImageName()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g2d.drawImage(img, null, fightTerrain.getX() * 40, fightTerrain.getY() * 40);
	}

}