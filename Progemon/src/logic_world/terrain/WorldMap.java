package logic_world.terrain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import graphic.DrawingUtility;
import graphic.IRenderable;
import graphic.IRenderableHolder;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class WorldMap implements IRenderable {

	private String name;
	private int[][] map;
	private static List<Image> tileset = new ArrayList<Image>();
	private static final String DEFAULT_PATH = "load\\img\\world\\tileset.png";
	private static WritableImage wimg;
	private boolean visible = true;
	private List<WorldObject> worldObjects = new ArrayList<>();
	private List<IRenderable> visibleWorldObjects = new ArrayList<>();

	private static Properties defaultProperties = new Properties();
	private Properties mapProperties = new Properties(defaultProperties);
	static {
		defaultProperties.setProperty("music", "littleroot");
	}

	private WorldObject space = WorldObject.createWorldObject("0", -1, -1, new ArrayList<>(), this);

	public WorldMap(String mapName) throws WorldMapException {
		this.name = mapName;
		String filePath = "load\\worldmap\\" + mapName + "\\" + mapName + "_map.csv";
		loadMap(filePath);

		space.hide();
		space.addOnEnter("-");
		space.addOnExit("-");
	}

	@Override
	public void draw() {
		DrawingUtility.drawWorldMap(this);
	}

	@Override
	public int getDepth() {
		return Integer.MIN_VALUE;
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

	public int getTerrainAt(int x, int y) throws WorldMapException {
		try {
			return map[y][x];
		} catch (NullPointerException ex) {
			throw new WorldMapException("Map not loaded", ex);
		} catch (IndexOutOfBoundsException ex) {
			throw new WorldMapException("Cannot find Terrain at [x=" + x + ", y=" + y + "]", ex);
		}
	}

	public int getTerrainAt(int x, int y, WorldDirection direction) throws WorldMapException {
		return getTerrainAt(x + direction.getX(), y + direction.getY());
	}

	public static Image getImage(int tileCode) {
		try {
			return tileset.get(tileCode);
		} catch (Exception ex) {
			throw new UnknownTileSetException(ex);
		}
	}

	public static List<Image> getTileset() {
		return tileset;
	}

	public int[][] getMap() {
		return map;
	}

	public int getHeight() {
		return map.length;
	}

	public int getWidth() {
		return map[0].length;
	}

	public void setMap(int[][] map) {
		this.map = map;
	}

	// worldObject List

	public void addWorldObjects(WorldObject worldObject) {
		worldObjects.add(worldObject);
	}

	public WorldObject getObjectAt(int x, int y) {
		for (WorldObject worldObject : worldObjects) {
			if (worldObject.getBlockX() == x && worldObject.getBlockY() == y) {
				return worldObject;
			}
		}
		return space;
	}

	public void clearWorldObjects() {
		worldObjects.clear();
	}

	// Load

	/** Loads the tileset image */
	public static void loadTileset() {
		Image img = new Image(new File(DEFAULT_PATH).toURI().toString());
		for (int i = 0; i < 40; i++) {
			tileset.add(DrawingUtility.resize(new WritableImage(img.getPixelReader(), (i % 10) * 16, Math.floorDiv(i, 10) * 16, 16, 16), 2));
		}
	}

	public void loadMap(String filePath) throws WorldMapException {
		try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(filePath)))) {
			String delimiter = ",";
			String[] widthAndHeight = scanner.nextLine().split(delimiter);
			int width = Integer.parseInt(widthAndHeight[0]);
			int height = Integer.parseInt(widthAndHeight[1]);
			Pattern pattern = Pattern.compile("[" + delimiter + "\\s+-?\\d+]+");
			Matcher matcher;
			map = new int[height][width];
			int[] mapLine = new int[width];
			int lineCounter = 0;
			// System.out.println("Map [filePath=" + filePath + "width=" + width
			// + ", height=" + height + "]");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.matches("^#+.*")) { // comment line ##hey hey heys
					continue;
				}
				matcher = pattern.matcher(line);
				if (matcher.find()) {
					int digitCounter = 0;
					for (String digit : line.trim().split("\\s*" + delimiter + "\\s*")) {
						mapLine[digitCounter] = Integer.parseInt(digit);
						digitCounter++;
					}
					map[lineCounter] = mapLine.clone();
					lineCounter++;
				} else {
					throw new WorldMapException("loadMap(): Unmatched pattern : \"" + line + "\", filePath=" + filePath
							+ ", lineCounter=" + lineCounter);
				}
			}
		} catch (FileNotFoundException ex) {
			throw new WorldMapException("World Map Load Error [filePath=" + filePath + " not found]", ex);
		} catch (NumberFormatException ex) {
			throw new WorldMapException("World Map Load Error [filePath=" + filePath + " not found]", ex);
		}
	}

	public void loadProperties(String filePath) throws WorldMapException {
		try (FileInputStream in = new FileInputStream(filePath)) {
			mapProperties.load(in);
		} catch (FileNotFoundException ex) {
			throw new WorldMapException("World Map Properties Load Error [filePath=" + filePath + " not found]", ex);
		} catch (IOException ex) {
			throw new WorldMapException("Exception on " + filePath, ex);
		}
	}

	public Properties getMapProperties() {
		return mapProperties;
	}

	public String getName() {
		return name;
	}

	public List<WorldObject> getWorldObjects() {
		return worldObjects;
	}

	public final List<IRenderable> getVisibleWorldObjects() {
		return visibleWorldObjects;
	}

	public void addVisibleWorldObject(WorldObject object) {
		visibleWorldObjects.add(object);
	}

	public void addAllVisibleWorldObject(List<IRenderable> objects) {
		visibleWorldObjects.addAll(objects);
	}

}
