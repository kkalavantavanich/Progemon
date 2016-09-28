package graphic;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.*;

import javax.swing.JComponent;

import logic.terrain.FightTerrain;

public class FightTerrainComponent extends JComponent{
	
	private int x, y;
	private FightTerrain.TerrainType type;
	
	public FightTerrainComponent(FightTerrain ft) {
		// TODO Auto-generated constructor stub
		x = ft.getX();
		y = ft.getY();
		type = ft.getType();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		Graphics2D g2 = (Graphics2D) g;
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(type.getImageName()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g2.drawImage(img, null, x * 40, y * 40);
	}

}
