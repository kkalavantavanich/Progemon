package logic.character;

import logic.terrain.FightMap;

public class HumanPlayer extends Player {

	public HumanPlayer(String name, Pokemon starter_pokemon) {
		super(name, starter_pokemon);
	}
	
	public HumanPlayer(String name){
		super(name);
	}

	@Override
	public void runTurn(Pokemon pokemon, FightMap fightMap) {
		// TODO Auto-generated method stub

	}

}