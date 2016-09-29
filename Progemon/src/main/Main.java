package main;

import java.io.IOException;

import graphic.Frame;
import graphic.ScreenComponent;
import logic.character.Pokemon;
import logic.terrain.FightMap;
import utility.FileUtility;
import utility.Pokedex;
import utility.RandomUtility;

import java.lang.Thread;

public class Main {
	
	public static void main(String[] args) {
		
		new Frame();
		
		int tick = 0;
		
		FightMap fightMap = null;
		try {
			fightMap = new FightMap(FileUtility.loadFightMap());
			FileUtility.loadPokedex();
			FileUtility.loadPokemons();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Pokemon a = Pokedex.getPokemon("Charlizard");
		Pokemon b = Pokedex.getPokemon("Ivysaur");
		
		ScreenComponent.addObject(fightMap);
		ScreenComponent.addObject(a);
		ScreenComponent.addObject(b);
		a.move(8, 8);
		b.move(7, 7);
		
		Frame.getGraphicComponent().repaint();
		
		while(true){
			tick++;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(tick == 100){
				tick = 0;
				a.move(RandomUtility.randomInt(7), RandomUtility.randomInt(5));
				b.move(RandomUtility.randomInt(7), RandomUtility.randomInt(5));
			}
			Frame.getGraphicComponent().repaint();
		}
	}

}
