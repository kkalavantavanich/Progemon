package logic_fight.terrain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import graphic.DrawingUtility;
import graphic.GameScreen;
import graphic.IRenderable;
import graphic.IRenderableHolder;
import javafx.scene.input.KeyCode;
import logic_fight.character.pokemon.Pokemon;
import logic_fight.filters.Filter;
import logic_fight.filters.MoveFilter;
import logic_fight.player.HumanPlayer;
import manager.GUIFightGameManager;
import manager.GUIFightGameManager.mouseRegion;
import utility.InputUtility;

public class FightMap implements IRenderable {

	public static final int MAX_SIZE_X = 20;
	public static final int MAX_SIZE_Y = 20;
	public static final int ORIGINAL_BLOCK_SIZE = 40;
	private static int originX, originY;
	private static int zoomLevel;
	private static int cursorX = -1, cursorY = -1;
	private int sizeX, sizeY;
	private FightTerrain[][] map;
	private ArrayList<Pokemon> pokemonsOnMap = new ArrayList<Pokemon>();
	private ArrayList<Pokemon> playerPokemonsOnMap = new ArrayList<Pokemon>(3);
	private ArrayList<Pokemon> enemyPokemonsOnMap = new ArrayList<Pokemon>(3);
	private boolean visible = true, drag = false;

	public static enum Direction {
		UP(0, -1), LEFT(-1, 0), DOWN(0, 1), RIGHT(1, 0);

		public int x, y;

		Direction(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public FightMap(int sizeX, int sizeY) {
		if (sizeX < 0) {
			sizeX = 0;
		} else if (sizeX > MAX_SIZE_X) {
			sizeX = MAX_SIZE_X;
		}
		if (sizeY < 0) {
			sizeY = 0;
		} else if (sizeY > MAX_SIZE_Y) {
			sizeY = MAX_SIZE_Y;
		}
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		map = new FightTerrain[sizeY][sizeX];
		originX = 100;
		originY = 100;
		zoomLevel = 0;
	}

	public FightMap(FightTerrain[][] ft) {
		map = ft;
		sizeX = map[0].length;
		sizeY = map.length;
		originX = 100;
		originY = 100;
		zoomLevel = 0;
	}

	/** Sort by Speed. Used in calculation of Turn Time and Queue. */
	public void sortPokemons() {
		Collections.sort(pokemonsOnMap, Pokemon.bySpeed);
	}

	public FightTerrain[][] getMap() {
		return map;
	}

	public void setMap(FightTerrain[][] map) {
		this.map = map;
	}

	public boolean contains(FightTerrain fightTerrain) {
		return Stream.of(map).flatMap(line -> Stream.of(line)).anyMatch(ft -> ft.equals(fightTerrain));
	}

	public FightTerrain getFightTerrainAt(int x, int y) {
		if (outOfMap(x, y)) {
			return null;
		}
		return map[y][x];
	}

	public FightTerrain getFightTerrainAt(FightTerrain paramFT, Direction d) {
		return getFightTerrainAt(paramFT.getX() + d.x, paramFT.getY() + d.y);
	}

	public void setFightTerrainAt(int x, int y, FightTerrain fightTerrain) {
		map[y][x] = fightTerrain;
	}

	public Optional<Pokemon> getPokemonAt(int x, int y) {
		for (Pokemon pokemon : pokemonsOnMap) {
			if (pokemon.getCurrentFightTerrain().getX() == x && pokemon.getCurrentFightTerrain().getY() == y) {
				return Optional.of(pokemon);
			}
		}
		return Optional.empty();
	}

	public Optional<Pokemon> getPokemonAt(FightTerrain fightTerrain) {
		if (fightTerrain == null) {
			return Optional.empty();
		} else {
			return getPokemonAt(fightTerrain.getX(), fightTerrain.getY());
		}
	}

	public boolean outOfMap(int x, int y) {
		if (x < 0 || x >= this.sizeX || y < 0 || y >= this.sizeY) {
			return true;
		} else {
			return false;
		}
	}

	// Graphics

	public void draw() {
		DrawingUtility.drawFightMap(this);
	}

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void hide() {
		visible = false;
		IRenderableHolder.removeWorldObject(this);
	}

	@Override
	public void show() {
		IRenderableHolder.addWorldObject(this);
		visible = true;
	}

	public final int getSizeX() {
		return sizeX;
	}

	public final int getSizeY() {
		return sizeY;
	}

	/** @return Unmodifiable List of Pokemons on Map. */
	public final List<Pokemon> getPokemonsOnMap() {
		return Collections.unmodifiableList(pokemonsOnMap);
	}

	/**
	 * Use this to add Pokemon to map. Returns true if pokemon can be placed and
	 * places the pokemon.
	 */
	public boolean addPokemonToMap(int x, int y, Pokemon pokemon) {
		Filter canBePlacedFilter = new MoveFilter();
		if (!outOfMap(x, y) && pokemon != null && canBePlacedFilter.check(pokemon, this, map[y][x])
				&& !this.getPokemonAt(x, y).isPresent()) {
			// Can be Added!
			pokemon.setCurrentFightMap(this);
			pokemon.move(x, y);
			pokemonsOnMap.add(pokemon);
			if (pokemon.getOwner() instanceof HumanPlayer) {
				playerPokemonsOnMap.add(pokemon);
			} else {
				enemyPokemonsOnMap.add(pokemon);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Use this to remove Pokemon from map. Returns true if pokemon is on map.
	 */
	public boolean removePokemonFromMap(Pokemon pokemon) {
		if (pokemonsOnMap.contains(pokemon)) {
			pokemon.move(null);
			pokemon.setCurrentFightMap(null);
			pokemonsOnMap.remove(pokemon);
			return true;
		} else {
			return false;
		}
	}

	public void shadowAllBlocks() {
		for (int j = 0; j < sizeY; j++) {
			for (int i = 0; i < sizeX; i++) {
				map[j][i].setShadowed(true);
			}
		}
	}

	public void highlightAllBlocks() {
		for (Pokemon p : pokemonsOnMap) {
			FightTerrain ft = p.getCurrentFightTerrain();
			ft.setHighlight(true);
		}
	}

	public void unshadowAllBlocks() {
		for (int j = 0; j < sizeY; j++) {
			for (int i = 0; i < sizeX; i++) {
				map[j][i].setShadowed(false);
				map[j][i].setHighlight(false);
			}
		}
	}

	public ArrayList<Pokemon> getPlayerPokemonsOnMap() {
		return playerPokemonsOnMap;
	}

	public ArrayList<Pokemon> getEnemyPokemonsOnMap() {
		return enemyPokemonsOnMap;
	}

	public void checkInput() {
		if (!InputUtility.isMouseLeftPress()) {
			drag = false;
		}
		if (drag) {
			setOrigin(InputUtility.getDragX() + originX, InputUtility.getDragY() + originY);
		}
		if (GUIFightGameManager.instance.getCurrentMouseRegion() == mouseRegion.FIGHTMAP) {
			int blockSize = getBlockSize();
			setZoomLevel(zoomLevel + InputUtility.getScrollUp() - InputUtility.getScrollDown());
			double x = InputUtility.getMouseX() - originX, y = InputUtility.getMouseY() - originY;
			if (InputUtility.isMouseLeftClick()) {
				drag = true;
			}
			if (0 <= x && x < sizeX * blockSize && 0 <= y && y < sizeY * blockSize) {
				cursorX = (int) Math.floor(x / blockSize);
				cursorY = (int) Math.floor(y / blockSize);
				getFightTerrainAt(cursorX, cursorY).setCursor(true);
				return;
			}
		}
		cursorX = -1;
		cursorY = -1;
	}

	public static int getOriginX() {
		return originX;
	}

	public static int getOriginY() {
		return originY;
	}

	public void setOrigin(int x, int y) {
		int blockSize = getBlockSize();
		if (x + 0.5 * blockSize > GameScreen.WIDTH / 2) {
			originX = (GameScreen.WIDTH - blockSize) / 2;
		} else if (x + (sizeX - 0.5) * blockSize < GameScreen.WIDTH / 2) {
			originX = (int) (GameScreen.WIDTH / 2 - (sizeX - 0.5) * blockSize);
		} else {
			originX = x;
		}
		if (y + 0.5 * blockSize > GameScreen.HEIGHT / 2) {
			originY = (GameScreen.HEIGHT - blockSize) / 2;
		} else if (y + (sizeY + 0.25) * blockSize < GameScreen.HEIGHT / 2) {
			originY = (int) (GameScreen.HEIGHT / 2 - (sizeY + 0.25) * blockSize);
		} else {
			originY = y;
		}
	}

	public static int getZoomLevel() {
		return zoomLevel;
	}

	public static void setZoomLevel(int zoomLevel) {
		if (zoomLevel > 3) {
			zoomLevel = 3;
		} else if (zoomLevel < -6) {
			zoomLevel = -6;
		} else {
			FightMap.zoomLevel = zoomLevel;
		}
	}

	public static int getBlockSize() {
		return (int) (ORIGINAL_BLOCK_SIZE * Math.pow(1.1, zoomLevel));
	}

	public static int getCursorX() {
		return cursorX;
	}

	public static int getCursorY() {
		return cursorY;
	}

}
