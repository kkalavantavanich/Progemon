package main;

import java.io.IOException;

import graphic.DrawingUtility;
import graphic.Frame;
import logic.terrain.FightMap;
import utility.FileUtility;

public class Main {
	
	public static void main(String[] args) {
		
		new Frame();
		
		FightMap fightMap = null;
		try {
			fightMap = new FightMap(FileUtility.loadFightMap());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DrawingUtility.addObject(fightMap);
		Frame.getGraphicComponent().update(Frame.getGraphicComponent().getGraphics());
	}

}
