package logic.player;

import graphic.Frame;
import logic.character.Pokemon;
import logic.filters.AttackFilter;
import logic.filters.MoveFilter;
import logic.terrain.Path;
import utility.Clock;
import utility.InputUtility;
import utility.RandomUtility;

public class AIPlayer extends Player {

	private Path nextPath;
	private int moveCounter = 1;
	private int thinkDelay = 30, thinkDelayCounter = 0;
	private int moveDelay = 5, moveDelayCounter = 0;

	// Constructors

	public AIPlayer(String name, Pokemon starter_pokemon) {
		super(name, starter_pokemon);
	}

	public AIPlayer(String name) {
		super(name);
	}

	@Override
	public void pokemonMove(Pokemon pokemon) {
		pokemon.findBlocksAround(pokemon.getMoveRange(), new MoveFilter());
		pokemon.sortPaths();
		pokemon.shadowBlocks();

		inputMove(pokemon);
		move(pokemon);
	}
	
	protected void inputMove(Pokemon pokemon) {
		boolean input = false;
		while (!input) {
			if (thinkDelayCounter == thinkDelay) {
				// LinkedList<FightTerrain> nextPath =
				// RandomUtility.randomElement(pokemon.getPaths());
				nextPath = calculateNextPath(pokemon);
				input = true;
				thinkDelayCounter = 0;
				pokemon.getCurrentFightMap().unshadowAllBlocks();
			} else {
				thinkDelayCounter++;
			}
			
			Frame.getGraphicComponent().repaint();
			Clock.tick();
		}
	}
	
	protected final void move(Pokemon pokemon) {
		boolean move = false;
		int x = pokemon.getCurrentFightTerrain().getX();
		int y = pokemon.getCurrentFightTerrain().getY();
		
		while (!move) {
			if(moveCounter == nextPath.size()){
				System.out.println("Pokemon " + pokemon.getName() + " moved from (" + x + ", " + y + ") to ("
						+ pokemon.getCurrentFightTerrain().getX() + ", " + pokemon.getCurrentFightTerrain().getY() + ").");
				moveCounter = 1;
				move = true;
			}
			else if(moveDelay == moveDelayCounter){
				if(nextPath.get(moveCounter) != pokemon.getCurrentFightTerrain()){
					pokemon.move(nextPath.get(moveCounter));
				} else {
					moveCounter++;
				}
				moveDelayCounter = 0;
			}
			else{
				moveDelayCounter++;
			}
			
			Frame.getGraphicComponent().repaint();
			Clock.tick();
		}
		
		while (moveDelayCounter != moveDelay) {
			moveDelayCounter++;
			Frame.getGraphicComponent().repaint();
			Clock.tick();
		}
		
		moveDelayCounter = 0;
	}
	
	/** This can be overrided by other AIs */
	protected Path calculateNextPath(Pokemon pokemon) {
		return pokemon.getPaths().get(0);
	}

	@Override
	/**
	 * @param attackingPokemon
	 *            The <code>Pokemon</code> that is used to attack other
	 *            <code>Pokemon</code>.
	 */
	public void pokemonAttack(Pokemon attackingPokemon) {
		// attack
		attackingPokemon.findBlocksAround(attackingPokemon.getAttackRange(), new AttackFilter());
		attackingPokemon.sortPaths();
		attackingPokemon.shadowBlocks();
		for (Pokemon other : attackingPokemon.getCurrentFightMap().getPokemonsOnMap()) {
			if (other.getOwner() != this
					&& attackingPokemon.getAvaliableFightTerrains().contains(other.getCurrentFightTerrain())) {
				// If other is enemy and in attack range.
				other.getCurrentFightTerrain().setHighlight(true);
			}
		}
		
		Pokemon other = inputAttack(attackingPokemon);
		attack(attackingPokemon, other);
	}
	
	protected Pokemon inputAttack(Pokemon attackingPokemon){
		while (true) {
			if (thinkDelayCounter == thinkDelay) {
				for (Pokemon other : attackingPokemon.getCurrentFightMap().getPokemonsOnMap()) {
					if (other.getOwner() != this
							&& attackingPokemon.getAvaliableFightTerrains().contains(other.getCurrentFightTerrain())) {
						// If other is enemy and in attack range.
						thinkDelayCounter = 0;
						attackingPokemon.getCurrentFightMap().unshadowAllBlocks();
						return other;
					}
				}
				thinkDelayCounter = 0;
				attackingPokemon.getCurrentFightMap().unshadowAllBlocks();
				return null;
			}
			else {
				thinkDelayCounter++;
			}
			
			Frame.getGraphicComponent().repaint();
			Clock.tick();
		}
	}
	
	protected final void attack(Pokemon attackingPokemon, Pokemon other) {
		boolean attack = false;
		if (other == null) {
			attack = true;
		}
		while (!attack) {
			attackingPokemon.attack(other, RandomUtility.randomInt(attackingPokemon.getActiveSkills().size() - 1));
			attack = true;
			
			Frame.getGraphicComponent().repaint();
			Clock.tick();
		}
	}
}
