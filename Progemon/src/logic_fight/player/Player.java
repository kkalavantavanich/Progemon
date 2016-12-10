package logic_fight.player;

import java.util.ArrayList;
import java.util.Optional;

import graphic.FightHUD;
import javafx.scene.paint.Color;
import logic_fight.FightPhase;
import logic_fight.character.activeSkill.ActiveSkill;
import logic_fight.character.pokemon.Pokemon;
import logic_fight.filters.AttackFilter;
import logic_fight.filters.MoveFilter;
import logic_fight.terrain.Path;
import manager.GUIFightGameManager;
import utility.Clock;
import utility.RandomUtility;
import utility.exception.AbnormalPhaseOrderException;
import utility.exception.UnknownPhaseException;

public abstract class Player {
	private String name;
	private Color color;
	private ArrayList<Pokemon> pokemons;
	private boolean godlike;

	protected Optional<Pokemon> nextAttackedPokemon;
	protected Optional<ActiveSkill> nextAttackSkill;
	protected Pokemon captureTarget;

	protected Optional<Path> nextPath;
	private int moveCounter = 1;
	private int moveDelay = 5, moveDelayCounter = 0;
	private int tokenx, tokeny;

	private GUIFightGameManager currentFightManager;

	// Constructor

	protected Player(String name) {
		this.name = name;
		color = Color.BLACK; // default
		pokemons = new ArrayList<Pokemon>();
	}

	protected Player(String name, Color color) {
		this.name = name;
		this.color = color;
		pokemons = new ArrayList<Pokemon>();
	}

	protected Player(String name, Pokemon starter_pokemon, Color color) {
		this(name, color);
		starter_pokemon.setOwner(this);
		pokemons.add(starter_pokemon);
	}

	protected Player(String name, Pokemon[] pokemon_set, Color color) {
		this(name, color);
		for (Pokemon pokemon : pokemon_set) {
			pokemon.setOwner(this);
			pokemons.add(pokemon);
		}
	}

	// Run turn

	/** Each turn calls this. Finite-State Machine */
	public final void runTurn(Pokemon pokemon) {
		boolean phaseIsFinished = false;
		try {
			while (currentFightManager.getCurrentPhase() != FightPhase.endPhase) {
				try {
					FightPhase currentPhase = currentFightManager.getCurrentPhase();
					// System.out.println("currentPhase = " + currentPhase);
					switch (currentPhase) {
					case initialPhase:
						nextPath = null;
						nextAttackedPokemon = Optional.empty();
						nextAttackSkill = Optional.empty();
						captureTarget = null;
						phaseIsFinished = true;
						break;

					case preMovePhase:
						pokemon.getCurrentFightMap().unshadowAllBlocks();
						pokemon.findBlocksAround(pokemon.getMoveRange(), new MoveFilter());
						pokemon.sortPaths();
						pokemon.shadowBlocks();
						tokenx = pokemon.getCurrentFightTerrain().getX();
						tokeny = pokemon.getCurrentFightTerrain().getY();
						phaseIsFinished = true;
						break;
					case inputMovePhase:
						phaseIsFinished = inputNextPath(pokemon);
						break;
					case movePhase:
						phaseIsFinished = move(pokemon);
						break;
					case postMovePhase:
						pokemon.getCurrentFightMap().unshadowAllBlocks();
						phaseIsFinished = true;
						break;
					case preAttackPhase:
						FightHUD.setCurrentPokemon(pokemon);
						pokemon.findBlocksAround(pokemon.getAttackRange(), new AttackFilter());
						pokemon.sortPaths();
						pokemon.shadowBlocks();
						pokemon.getCurrentFightMap().getPokemonsOnMap().stream()
								.filter((Pokemon other) -> !pokemon.getOwner().equals(other.getOwner()))
								.filter((Pokemon other) -> pokemon.getAvaliableFightTerrains()
										.contains(other.getCurrentFightTerrain()))
								.forEach((Pokemon other) -> other.getCurrentFightTerrain().setHighlight(true));
						phaseIsFinished = true;
						break;
					case inputAttackPhase:
						boolean check1 = inputAttackPokemon(pokemon);
						boolean check2 = inputAttackActiveSkill(pokemon);
						phaseIsFinished = check1 && check2;
						break;
					case attackPhase:
						phaseIsFinished = attack(pokemon, nextAttackedPokemon, nextAttackSkill);
						break;
					case postAttackPhase:
						pokemon.getCurrentFightMap().unshadowAllBlocks();
						phaseIsFinished = true;
						break;
					case endPhase:
						phaseIsFinished = true;
						break;
					case preCapturePhase:
						pokemon.getCurrentFightMap().unshadowAllBlocks();
						pokemon.getCurrentFightMap().shadowAllBlocks();
						pokemon.getCurrentFightMap().getPokemonsOnMap().stream()
								.filter(p -> !p.getOwner().equals(pokemon.getOwner()))
								.map(Pokemon::getCurrentFightTerrain).forEach(ft -> ft.setHighlight(true));
						phaseIsFinished = true;
						break;
					case inputCapturePhase:
						phaseIsFinished = inputCapturePokemon(pokemon);
						break;
					case capturePhase:
						if (captureTarget == null) {
							System.err.println("Capture Target not Set!");
							break;
						}
						pokemon.getCurrentFightMap().unshadowAllBlocks();
						captureTarget.getCurrentFightTerrain().setHighlight(true);
						boolean captured = RandomUtility.randomPercent(100) <= 0.2;
						if (captured) {
							pokemon.getCurrentFightMap().removePokemonFromMap(captureTarget);
							captureTarget.getOwner().removePokemon(captureTarget);
							pokemon.getOwner().addPokemon(captureTarget);
							System.out.println("(Player.java:145) CAPTURED!");
						} else {
							System.out.println("(Player.java:147) NOT CAPTURED!");
						}
						phaseIsFinished = true;
						break;
					case postCapturePhase:
						pokemon.getCurrentFightMap().unshadowAllBlocks();
						phaseIsFinished = true;
						break;
					default:
						throw new UnknownPhaseException("Unknown Phase in Player.java[" + this.name + "].");
					}
					if (phaseIsFinished) {
						currentFightManager.setNextPhase(currentPhase.nextPhase());
					} else {
						currentFightManager.setNextPhase(currentPhase);
					}
				} catch (AbnormalPhaseOrderException e) {
					currentFightManager.setNextPhase(e.getNextPhase());
				}
				currentFightManager.nextPhase();
				Clock.tick();
			}
		} catch (UnknownPhaseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nextAttackedPokemon = null;
			nextAttackSkill = null;
		}

		// pokemonMove(pokemon);
		// pokemonAttack(pokemon);
	}

	protected abstract boolean inputNextPath(Pokemon pokemon) throws AbnormalPhaseOrderException;

	protected final boolean move(Pokemon pokemon) {
		// <<<<<<< HEAD
		// if (moveCounter == nextPath.get().size()) {
		// System.out.println("Pokemon " + pokemon.getName() + " moved from (" +
		// x + ", " + y + ") to ("
		// =======
		if (moveCounter == nextPath.get().size()) {
			System.out.println("Pokemon " + pokemon.getName() + " moved from (" + tokenx + ", " + tokeny + ") to ("
			// >>>>>>> 510768d529f4fdb8b001f678c37730aaa55ef038
					+ pokemon.getCurrentFightTerrain().getX() + ", " + pokemon.getCurrentFightTerrain().getY() + ").");
			moveCounter = 1;
			moveDelayCounter = 0;
			return true;
		} else if (moveDelay == moveDelayCounter) {
			if (nextPath.get().get(moveCounter) != pokemon.getCurrentFightTerrain()) {
				pokemon.move(nextPath.get().get(moveCounter));
			} else {
				moveCounter++;
			}
			moveDelayCounter = 0;
			return false;
		} else {
			moveDelayCounter++;
			return false;
		}
	}

	protected abstract boolean inputAttackPokemon(Pokemon pokemon);

	protected abstract boolean inputAttackActiveSkill(Pokemon attackingPokemon) throws AbnormalPhaseOrderException;

	protected final boolean attack(Pokemon attackingPokemon, Optional<Pokemon> other,
			Optional<ActiveSkill> activeSkill) {
		if (other.isPresent() && activeSkill.isPresent()) {
			System.out.println("attacking... ");
			attackingPokemon.attack(other.get(), activeSkill.get());
		}
		return true;
	}

	protected boolean inputCapturePokemon(Pokemon pokemon) throws AbnormalPhaseOrderException {
		return true;
	}

	/** Checks if this player loses (All pokemons are dead) */
	public boolean isLose() {
		return pokemons.stream().allMatch(p -> p.isDead()) || pokemons.isEmpty();
	}

	// Getters

	public final String getName() {
		return name;
	}

	public final ArrayList<Pokemon> getPokemons() {
		return pokemons;
	}

	public void addPokemon(Pokemon pokemon) {
		pokemon.setOwner(this);
		pokemons.add(pokemon);
	}

	public void removePokemon(Pokemon pokemon) {
		pokemon.setOwner(null);
		pokemons.remove(pokemon);
	}

	public final Color getColor() {
		return color;
	}

	public final boolean isGodlike() {
		return godlike;
	}

	public final void setGodlike(boolean godlike) {
		this.godlike = godlike;
	}

	public final GUIFightGameManager getCurrentFightManager() {
		return currentFightManager;
	}

	public final void setCurrentFightManager(GUIFightGameManager currentFightManager) {
		this.currentFightManager = currentFightManager;
	}

}
