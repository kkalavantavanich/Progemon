package logic_world.terrain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import audio.MusicUtility;
import graphic.Animation;
import graphic.DrawingUtility;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import logic_fight.player.Player;
import logic_world.player.PlayerCharacter;
import manager.GUIFightGameManager;
import manager.WorldManager;
import utility.AnimationUtility;
import utility.Clock;

public class WorldObject extends Animation implements Cloneable {

	private static final String WORLD_OBJECTS_PROP_FILE = "load\\worldobjects_list.csv";
	private static final String DEFAULT_IMG_PATH = "load\\img\\world\\worldobjects.png";
	private static final String DEFAULT_IMGPOS_PATH = "load\\img\\world\\imageposition.csv";
	
	private static Map<String, WorldObject> allWorldObjects = new HashMap<>();
	private static Map<String, WorldObjectAction> allObjectFunctions = new HashMap<>();
	private static Map<String, ArrayList<Image>> objectImagesSet = new HashMap<>();

	protected int blockX, blockY;
	protected String objectCode;
	private ArrayList<WorldObjectAction> onEnter = new ArrayList<>(), onExit = new ArrayList<>(),
			onStep = new ArrayList<>(), onInteract = new ArrayList<>();
	/** Use to tell graphicDepth if objects overlap in worldMap. */
	private int specialDepth = 0;
	private ArrayList<ArrayList<String>> functionParameter = new ArrayList<>(4);
	private int actionType = 0, parameterCounter = 0;

	public static WorldObject createWorldObject(String objectCode, int blockX, int blockY, ArrayList<String> parameters,
			WorldMap owner) {
		try {
			WorldObject worldObject;
			worldObject = (WorldObject) allWorldObjects.get(objectCode).clone();
			worldObject.blockX = blockX;
			worldObject.blockY = blockY;
			for (int i = 0; i < 4; i++) {
				worldObject.functionParameter.add(new ArrayList<>());
			}
			if (!parameters.isEmpty()) {
				for (String string : parameters) {
					worldObject.functionParameter.get(Integer.parseInt(string.substring(0, 1)))
							.add(string.substring(string.indexOf("[") + 1, string.indexOf("]")));
				}
			}
			if (allWorldObjects.get(objectCode).isPlaying()) {
				worldObject.play();
			}
			if (!allWorldObjects.get(objectCode).isVisible()) {
				worldObject.visible = false;
			} else {
				worldObject.visible = true;
				owner.addVisibleWorldObject(worldObject);
			}

			owner.addWorldObjects(worldObject);
			return worldObject;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public WorldObject(String objectCode, int frameNumber, int frameDelay, boolean loop, boolean autostop,
			int specialDepth) {
		super(frameNumber, frameDelay, loop, autostop);
		this.objectCode = objectCode;
		this.specialDepth = specialDepth;
		addOnEnter("-");
		addOnStep("-");
		addOnExit("-");
	}

	public WorldObject(String objectCode, String onEnter, String onStep, String onExit, String onInteract,
			int frameNumber, int frameDelay, boolean loop, boolean autostop) {
		// TODO Auto-generated constructor stub
		super(frameNumber, frameDelay, loop, autostop);
		this.objectCode = objectCode;
		addOnEnter(onEnter);
		addOnStep(onStep);
		addOnExit(onExit);
	}

	public WorldObject(String[] args) {
		super(Integer.parseInt(args[5]), Integer.parseInt(args[6]), Boolean.parseBoolean(args[7]),
				Boolean.parseBoolean(args[8]));
		currentFrame = 0;
		objectCode = args[0];
		addOnEnter(args[1]);
		addOnStep(args[2]);
		addOnExit(args[3]);
		addOnInteract(args[4]);
		specialDepth = Integer.parseInt(args[11]);
		super.setPlaying(Boolean.parseBoolean(args[9]));
		super.setVisible(Boolean.parseBoolean(args[10]));
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		WorldObject worldObject = new WorldObject(objectCode, amountOfFrame, frameDelay, loop, autostop, specialDepth);
		worldObject.setOnEnter(onEnter);
		worldObject.setOnInteract(onInteract);
		worldObject.setOnStep(onStep);
		worldObject.setOnExit(onExit);
		return worldObject;
	}

	@Override
	public Image getCurrentImage() {
		if (objectImagesSet.get(objectCode) == null) {
			throw new NullPointerException("getCurrentImage : Can't find objectCode=" + objectCode);
		}
		return objectImagesSet.get(objectCode).get(currentFrame);
	}

	@Override
	public void draw() {
		DrawingUtility.drawWorldObject(this);
	}

	@Override
	public int getDepth() {
		return blockY * 32 + specialDepth;
	}

	public int getBlockX() {
		return blockX;
	}

	public void setBlockX(int blockX) {
		this.blockX = blockX;
	}

	public int getBlockY() {
		return blockY;
	}

	public void setBlockY(int blockY) {
		this.blockY = blockY;
	}

	public void setSpecialDepth(int specialDepth) {
		this.specialDepth = specialDepth;
	}

	public void addOnEnter(String actionCodes) {
		for (String actionCode : actionCodes.split("/")) {
			this.onEnter.add(allObjectFunctions.get(actionCode));
		}
	}

	public void setOnEnter(ArrayList<WorldObjectAction> onEnter) {
		this.onEnter = onEnter;
	}

	public void entered() {
		parameterCounter = 0;
		actionType = 0;
		for (WorldObjectAction function : onEnter) {
			function.execute(this);
		}
	}

	public void addOnInteract(String actionCodes) {
		for (String actionCode : actionCodes.split("/")) {
			this.onInteract.add(allObjectFunctions.get(actionCode));
		}
	}

	public void setOnInteract(ArrayList<WorldObjectAction> onInteract) {
		this.onInteract = onInteract;
	}

	public void interacted() {
		parameterCounter = 0;
		actionType = 1;
		for (WorldObjectAction function : onInteract) {
			function.execute(this);
		}
	}

	public void addOnStep(String actionCodes) {
		for (String actionCode : actionCodes.split("/")) {
			this.onStep.add(allObjectFunctions.get(actionCode));
		}
	}

	public void setOnStep(ArrayList<WorldObjectAction> onStep) {
		this.onStep = onStep;
	}

	public void step() {
		parameterCounter = 0;
		actionType = 2;
		for (WorldObjectAction function : onStep) {
			function.execute(this);
		}
	}

	public void addOnExit(String actionCodes) {
		for (String actionCode : actionCodes.split("/")) {
			this.onExit.add(allObjectFunctions.get(actionCode));
		}
	}

	public void setOnExit(ArrayList<WorldObjectAction> onExit) {
		this.onExit = onExit;
	}

	public void exit() {
		parameterCounter = 0;
		actionType = 3;
		for (WorldObjectAction function : onExit) {
			function.execute(this);
		}
	}

	// WorldDirection use incase owner map is not current center.
	/** load objects on map */
	public static void loadMapObjects(String datapath, WorldMap owner, int offsetX, int offsetY, int minX, int minY,
			int maxX, int maxY) {
		String delimiter = "\\s*,\\s*";
		try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(datapath)))) {
			Pattern pattern = Pattern.compile(String.join(delimiter, "(?<objectCode>\\d+)", "(?<blockX>\\d+)",
					"(?<blockY>\\d+)(?<functionParam>(", "\\d\\[.+\\])+)?"));
			Matcher matcher;
			ArrayList<String> functionsStr = new ArrayList<String>();
			loop: while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.matches("^#+.*")) { // comment line ##hey hey heys
					continue loop;
				}
				matcher = pattern.matcher(line);
				if (matcher.find()) {
					String objectCode = matcher.group("objectCode");
					int blockX = Integer.parseInt(matcher.group("blockX"));
					int blockY = Integer.parseInt(matcher.group("blockY"));
					if (blockX < minX || blockX > maxX || blockY < minY || blockY > maxY) {
						if (!objectCode.equals("001") && !objectCode.equals("002")) {
							System.out.println(
									"WorldObject.java : Rejected object " + objectCode + " in " + owner.getName());
							System.out.println("[blockX=" + blockX + ", blockY=" + blockY + ", x in [" + minX + ", "
									+ maxX + "], y in [" + minY + ", " + maxY + "] ]");
						}
						continue loop;
					}
					blockX += offsetX;
					blockY += offsetY;
					if (matcher.group("functionParam") != null && !matcher.group("functionParam").isEmpty()) {
						functionsStr.clear();
						for (String string : matcher.group("functionParam").trim().split(delimiter)) {
							if (!string.isEmpty()) {
								functionsStr.add(string);
							}
						}
					}
					createWorldObject(objectCode, blockX, blockY, functionsStr, owner);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * load all object template
	 * 
	 * @throws WorldMapException
	 */
	public static void loadWorldObjects() throws WorldMapException {
		try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(WORLD_OBJECTS_PROP_FILE)))) {
			String delimiter = "\\s*,\\s*";
			Pattern pattern = Pattern.compile(String.join(delimiter, "(?<objectCode>\\d+)",
					"(?<onEnter>\\w+[/\\w+]*|-)", "(?<onStep>\\w+[/\\w+]*|-)", "(?<onExit>\\w+[/\\w+]*|-)",
					"(?<onInteract>\\w+[/\\w+]*|-)", "(?<frameNumber>\\d+)", "(?<frameDelay>\\d+)", "(?<loop>\\w+)",
					"(?<autoStop>\\w+)", "(?<playing>\\w+)", "(?<showing>\\w+)", "(?<specialDepth>-?\\d+)"));
			Matcher matcher;
			String[] args = new String[12];
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.matches("^#+.*")) { // comment line ##hey hey heys
					continue;
				}
				matcher = pattern.matcher(line);
				if (matcher.find()) {
					for (int i = 0; i < 12; i++) {
						args[i] = matcher.group(i + 1);
					}
					allWorldObjects.put(args[0], new WorldObject(args));
				} else {
					throw new WorldMapException("loadWorldObjects() : Unmatched pattern=" + line);
				}
			}
		} catch (FileNotFoundException e) {
			throw new WorldMapException("loadWorldObjects() : file not found " + WORLD_OBJECTS_PROP_FILE, e);
		}
	}

	public static void loadObjectImages() {
		Image img = new Image(new File(DEFAULT_IMG_PATH).toURI().toString());
		System.out.println(img);
		try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(DEFAULT_IMGPOS_PATH)))) {
			String delimiter = "\\s*,\\s*";
			Pattern pattern = Pattern.compile(String.join(delimiter, "(?<objectCode>\\d+)", "(?<xPos>\\d+)",
					"(?<yPos>\\d+)", "(?<width>\\d+)", "(?<height>\\d+)"));
			Matcher matcher;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.matches("^#+.*")) { // comment line ##hey hey heys
					continue;
				}
				matcher = pattern.matcher(line);
				if (matcher.find()) {
					int amountOfFrame = allWorldObjects.get(matcher.group("objectCode")).amountOfFrame;
					ArrayList<Image> array = new ArrayList<>();
					int xPos = Integer.parseInt(matcher.group("xPos")), yPos = Integer.parseInt(matcher.group("yPos")),
							width = Integer.parseInt(matcher.group("width")),
							height = Integer.parseInt(matcher.group("height"));
					for (int i = 0; i < amountOfFrame; i++) {
						array.add(DrawingUtility.resize(new WritableImage(img.getPixelReader(),
								xPos + (width / amountOfFrame) * i, yPos, (width / amountOfFrame), height), 2));
					}
					objectImagesSet.put(matcher.group("objectCode"), array);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// All Object Function
	public static void loadObjectFunctions() {
		allObjectFunctions.put("-", target -> {

		});

		allObjectFunctions.put("play", target -> {
			target.show();
			target.play();
			return;
		});

		allObjectFunctions.put("playback", target -> {
			target.show();
			target.setPlayback(true);
			target.play();
		});

		allObjectFunctions.put("playforward", target -> {
			target.show();
			target.setPlayback(false);
			target.play();
		});

		allObjectFunctions.put("delay", target -> {
			String parameter = target.functionParameter.get(target.actionType).get(target.parameterCounter);
			int delay = Integer.parseInt(parameter);
			while (delay > 0) {
				delay--;
				Clock.tick();
			}
			target.parameterCounter++;
		});

		allObjectFunctions.put("hide", target -> {
			target.hide();
		});

		allObjectFunctions.put("hideplayer", object -> {
			WorldManager.getPlayer().hide();
		});

		allObjectFunctions.put("showplayer", object -> {
			WorldManager.getPlayer().show();
		});

		allObjectFunctions.put("spawn", object -> {
			Random random = new Random();
			if (random.nextInt(100) < 8) {
				MusicUtility.playMusic("battle_wild");
				AnimationUtility.getLoadScreen01().show();
				AnimationUtility.getLoadScreen01().play();
				while (AnimationUtility.getLoadScreen01().isPlaying()) {
					Clock.tick();
				}

				System.out.println("to fightmap !!!");
				Set<Player> players = new HashSet<Player>();
				players.add(PlayerCharacter.getMe());
				new GUIFightGameManager(players);
				MusicUtility.playMusic(WorldManager.getWorldMap().getMapProperties().getProperty("music"), true);
			}
		});

		allObjectFunctions.put("changemap", object -> {
			String[] parameters = object.functionParameter.get(object.actionType).get(object.parameterCounter)
					.split("/");
			object.parameterCounter++;
			System.out.println("parameter passed [1] and [2]" + parameters[1] + " " + parameters[2]);
			WorldManager.changeWorld(parameters[0], Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2]),
					Boolean.parseBoolean(parameters[3]), parameters[4]);
		});

		allObjectFunctions.put("playerpause", object -> {
			WorldManager.getPlayer().pause();
		});

		allObjectFunctions.put("playerunpause", object -> {
			WorldManager.getPlayer().unpause();
		});

		allObjectFunctions.put("playerwalk", object -> {
			WorldManager.getPlayer().walk();
		});

		allObjectFunctions.put("loadmap", object -> {
			String[] parameters = object.functionParameter.get(object.actionType).get(object.parameterCounter)
					.split("/");
			object.parameterCounter++;
			try {
				WorldManager.setWorldMapBuffer(WorldManager.loadWorld(parameters[0]));
			} catch (WorldMapException ex) {
				ex.printStackTrace();
			}
		});

		allObjectFunctions.put("usemap", object -> {
			String[] parameters = object.functionParameter.get(object.actionType).get(object.parameterCounter)
					.split("/");
			object.parameterCounter++;
			try {
				WorldManager.useBufferedWorld(Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]));
			} catch (NumberFormatException | WorldMapException ex) {
				ex.printStackTrace();
			}
		});

	}

}
