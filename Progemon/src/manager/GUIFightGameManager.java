package manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import audio.MusicUtility;
import audio.SFXUtility;
import graphic.DialogBox;
import graphic.FightHUD;
import graphic.IRenderableHolder;
import graphic.QueueBox;
import javafx.scene.paint.Color;
import logic_fight.FightPhase;
import logic_fight.character.activeSkill.ActiveSkill;
import logic_fight.character.pokemon.Pokemon;
import logic_fight.player.AIPlayer;
import logic_fight.player.Player;
import logic_fight.terrain.FightMap;
import utility.AnimationUtility;
import utility.Clock;
import utility.FileUtility;
import utility.GlobalPhase;
import utility.Pokedex;
import utility.RandomUtility;
import utility.StringUtility;

public class GUIFightGameManager {

	public static GUIFightGameManager currentFightManager;

	private List<Player> players;
	private List<Player> currentPlayers;
	private FightMap fightMap = null;
	private Pokemon currentPokemon = null;
	private Player currentPlayer = null;
	private Player winnerPlayer = null;
	private FightPhase currentPhase, nextPhase;

	private boolean isWild;

	/** set of players including the wild player if isWild */
	public GUIFightGameManager(Set<Player> players, boolean isWild) {
		
		currentFightManager = this;
		this.isWild = isWild;

		// if (isWild) {
		// Player wildPlayer = new AIPlayer("God", Color.GREEN);
		// Pokemon pidgey = Pokedex.getPokemon("Pidgey");
		// pidgey.setLevel(3);
		// pidgey.addActiveSkill(ActiveSkill.getActiveSkill("Gust"));
		// wildPlayer.addPokemon(pidgey);
		// players.add(wildPlayer);
		// }

		this.players = new ArrayList<Player>(players);
		currentPlayers = new ArrayList<Player>(players);
		currentPhase = FightPhase.initialPhase;

		players.stream().forEach(player -> {
			System.out.print("[Player " + player.getName() + " :: ");
			System.out.print(player.getPokemons().stream().map(Pokemon::getName).collect(Collectors.joining(", ")));
			System.out.println("]");
		});

		players.stream().forEach(p -> p.setCurrentFightManager(this));
		fightMap = new FightMap(FileUtility.loadFightMap());
		spawnPokemons();
		fightMap.sortPokemons();

	}

	public void startFight() {
		GlobalPhase.setCurrentPhase(GlobalPhase.FIGHT);
		new Clock();

		IRenderableHolder.addFightObject(fightMap);
		IRenderableHolder.addFightObject(new DialogBox());
		IRenderableHolder.addFightObject(new QueueBox(this));
		new FightHUD();
		System.out.println("Added DialogBox and QueueBox");

		AnimationUtility.getLoadScreen01().setPlayback(true);
		AnimationUtility.getLoadScreen01().play();
		while (AnimationUtility.getLoadScreen01().isPlaying()) {
			Clock.tick();
		}
		AnimationUtility.getLoadScreen01().setPlayback(false);
		AnimationUtility.getLoadScreen01().hide();

		System.out.println("Fight Game loaded without problems.");
		DialogBox.sentMessage("Press '" + DialogBox.advancingKey.toString() + "' to start!");
		runFight();
		endFight();
	}

	private void runFight() {
		while (true) {

			currentPokemon = fightMap.getPokemonsOnMap().get(0);
			currentPlayer = currentPokemon.getOwner();
			currentPhase = FightPhase.initialPhase;
			currentPlayer.runTurn(currentPokemon); // gives control to player
			currentPokemon.calculateNextTurnTime();

			clearDeadPokemons();
			fightMap.sortPokemons();

			QueueBox.sort();

			if (checkWinner()) {
				break;
			}

			for (int i = 1; i <= 30; i++) {
				Clock.tick(); // wait between turns
			}

		}
		System.out.println("END OF RUN");
	}

	private void endFight() {
		MusicUtility.playMusic("victory_wild", false);
		System.out.println("The fight has ended.");
		System.out.println("The winner is " + winnerPlayer.getName());
		DialogBox.sentMessage("The winner is " + winnerPlayer.getName());

		while (true) {
			if (DialogBox.hasSentMessage()) {
				break;
			}
			QueueBox.sort();
			DialogBox.update();
			Clock.tick();
		}
		players.forEach(p -> p.setCurrentFightManager(null));
		GlobalPhase.setCurrentPhase(GlobalPhase.WORLD);
	}

	private void spawnPokemons() {
		int nextX, nextY;
		for (Player player : players) {
			// System.out.println(player.getName());
			for (Pokemon pokemon : player.getPokemons()) {
				Objects.requireNonNull(pokemon);
				do {
					nextX = RandomUtility.randomInt(fightMap.getSizeX() - 1);
					nextY = RandomUtility.randomInt(fightMap.getSizeY() - 1);
					// System.out.println(pokemon.getName() + " " + nextX + " "
					// + nextY);
				} while (!fightMap.addPokemonToMap(nextX, nextY, pokemon));
			}
		}
		System.out.println("Finish Spawning Pokemons");
	}

	private boolean checkWinner() {
		for (int i = currentPlayers.size() - 1; i >= 0; i--) {
			if (currentPlayers.get(i).isLose()) {
				currentPlayers.remove(i);
			}
		}

		if (currentPlayers.size() == 1) {
			winnerPlayer = currentPlayers.get(0);
		}
		if (currentPlayers.size() == 0) {
			// Draws
			winnerPlayer = null;
		}
		return winnerPlayer != null;

	}

	private void clearDeadPokemons() {
		double playerFactor = isWild ? 1 : 1.5;

		for (int i = fightMap.getPokemonsOnMap().size() - 1; i >= 0; i--) {
			Pokemon p = fightMap.getPokemonsOnMap().get(i);
			if (p.isDead()) {
				System.out.println(p.getName() + " is DEAD!");

				double expYield = p.getExpYield() * playerFactor * p.getLevel() / 7;
				p.getKiller().getOwner().getPokemons().stream().filter(pokemon -> !pokemon.isDead())
						.forEachOrdered(pokemon -> {
							Clock.delay(10);
							pokemon.addExp(expYield);
							Clock.delay(10);
						});
				System.out.println(p.getKiller().getOwner().getName() + "'s pokemon gained "
						+ StringUtility.formatDouble(expYield, 2) + " exp");

				fightMap.removePokemonFromMap(p);
				SFXUtility.playSound("pokemon_faints");
			}
		}
	}

	public boolean canCapturePokemon() {
		if (currentPlayers.stream().filter(p -> p != currentPlayer).flatMap(p -> p.getPokemons().stream())
				.count() == 1) {
			return true;
		} else {
			System.err.println("Cannot Capture Pokemon : conditions not met.");
			return false;
		}
	}

	public final List<Player> getPlayers() {
		return players;
	}

	public final List<Player> getCurrentPlayers() {
		return currentPlayers;
	}

	public final FightMap getFightMap() {
		return fightMap;
	}

	public final Pokemon getCurrentPokemon() {
		return currentPokemon;
	}

	public final Player getWinnerPlayer() {
		return winnerPlayer;
	}

	public final FightPhase getCurrentPhase() {
		return currentPhase;
	}

	public FightPhase getNextPhase() {
		return nextPhase;
	}

	public void setNextPhase(FightPhase nextPhase) {
		this.nextPhase = nextPhase;
	}

	public void nextPhase() {
		currentPhase = nextPhase;
	}

	public final boolean isWild() {
		return isWild;
	}

	public void checkInput() {
		fightMap.checkInput();
		FightHUD.checkInput();
	}

}
