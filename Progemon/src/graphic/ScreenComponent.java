package graphic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JComponent;

public class ScreenComponent extends JComponent {
	
	private static ArrayList<IRenderable> objectOnScreen = new ArrayList<IRenderable>();
	protected static Graphics2D g2;
	
	public ScreenComponent() {
		// TODO Auto-generated constructor stub
		setPreferredSize(new Dimension(800, 600));
		setDoubleBuffered(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		g2.setBackground(Color.BLACK);
		for (IRenderable object : objectOnScreen) {
			object.draw();
		}
	}
	
	public static ArrayList<IRenderable> getObjectOnScreen() {
		return objectOnScreen;
	}
	
	public static void addObject(IRenderable object) {
		objectOnScreen.add(object);
	}

}