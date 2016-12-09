package logic_world.terrain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import graphic.Animation;
import graphic.DrawingUtility;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import logic_fight.player.Player;
import logic_world.player.PlayerCharacter;
import manager.GUIFightGameManager;
import manager.WorldManager;
import utility.Clock;
import utility.Function;

public class WorldObject extends Animation implements Cloneable {
	
	protected int blockX, blockY;
	protected int objectCode;
	private static ArrayList<WorldObject> allWorldObjects = new ArrayList<>();
	private static ArrayList<Function<WorldObject>> allObjectFunctions = new ArrayList<>();
	private static ArrayList<ArrayList<Image>> objectImagesSet = new ArrayList<>();
	private static final String DEFAULT_IMG_PATH = "load\\img\\world\\worldobjects.png";
	private static final String DEFAULT_IMGPOS_PATH = "load\\img\\world\\imageposition.txt";
	private ArrayList<Function<WorldObject>> onEnter = new ArrayList<>(), onExit = new ArrayList<>(), onStep = new ArrayList<>(), onInteract = new ArrayList<>();
	private int specialDepth = 0;
	private ArrayList<ArrayList<String>> functionParameter = new ArrayList<>(4);
	private int actionType = 0, parameterCounter = 0;
	
	public static WorldObject createWorldObject(int objectCode, int blockX, int blockY, ArrayList<String> parameters) {
		// TODO Auto-generated constructor stub
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
					worldObject.functionParameter.get(Integer.parseInt(string.substring(0, 1))).add(string.substring(string.indexOf("[") + 1, string.indexOf("]")));
				}
			}
			if (allWorldObjects.get(objectCode).isPlaying()) {
				worldObject.play();
			}
			if (!allWorldObjects.get(objectCode).isVisible()) {
				worldObject.hide();
			}
			else{
				worldObject.show();
			}
			WorldManager.addWorldObjects(worldObject);
			return worldObject;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public WorldObject(int objectCode, int frameNumber, int frameDelay, boolean loop, boolean autostop, int specialDepth) {
		// TODO Auto-generated constructor stub
		super(frameNumber, frameDelay, loop, autostop);
		this.objectCode = objectCode;
		this.specialDepth = specialDepth;
		addOnEnter("-");
		addOnStep("-");
		addOnExit("-");
	}
	
	public WorldObject(int objectCode, String onEnter, String onStep, String onExit, String onInteract, int frameNumber, int frameDelay, boolean loop, boolean autostop) {
		// TODO Auto-generated constructor stub
		super(frameNumber, frameDelay, loop, autostop);
		this.objectCode = objectCode;
		addOnEnter(onEnter);
		addOnStep(onStep);
		addOnExit(onExit);
	}
	
	public WorldObject(String[] args) {
		super(Integer.parseInt(args[5]), Integer.parseInt(args[6]), Boolean.parseBoolean(args[7]), Boolean.parseBoolean(args[8]));
		currentFrame = 0;
		objectCode = Integer.parseInt(args[0]);
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
		// TODO Auto-generated method stub
		WorldObject worldObject = new WorldObject(objectCode, frameNumber, frameDelay, loop, autostop, specialDepth);
		worldObject.setOnEnter(onEnter);
		worldObject.setOnInteract(onInteract);
		worldObject.setOnStep(onStep);
		worldObject.setOnExit(onExit);
		return worldObject;
	}
	
	@Override
	public Image getCurrentImage() {
		// TODO Auto-generated method stub
		return objectImagesSet.get(objectCode).get(currentFrame);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		DrawingUtility.drawWorldObject(this);
	}

	@Override
	public int getDepth() {
		// TODO Auto-generated method stub
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
	
	public void addOnEnter(String string) {
		for (String str : string.split("/")) {
			for (Function<WorldObject> function : allObjectFunctions) {
				if (function.getName().equals(str)) {
					this.onEnter.add(function);
				}
			}
		}
	}
	
	public void setOnEnter(ArrayList<Function<WorldObject>> onEnter) {
		this.onEnter = onEnter;
	}
	
	public void entered() {
		parameterCounter = 0;
		actionType = 0;
		for (Function<WorldObject> function : onEnter) {
			function.execute(this);
		}
	}
	
	public void addOnInteract(String string) {
		for (String str : string.split("/")) {
			for (Function<WorldObject> function : allObjectFunctions) {
				if (function.getName().equals(str)) {
					this.onInteract.add(function);
				}
			}
		}
	}
	
	public void setOnInteract(ArrayList<Function<WorldObject>> onInteract) {
		this.onInteract = onInteract;
	}
	
	public void interacted() {
		parameterCounter = 0;
		actionType = 1;
		for (Function<WorldObject> function : onInteract) {
			function.execute(this);
		}
	}
	
	public void addOnStep(String string) {
		for (String str : string.split("/")) {
			for (Function<WorldObject> function : allObjectFunctions) {
				if (function.getName().equals(str)) {
					this.onStep.add(function);
				}
			}
		}
	}
	
	public void setOnStep(ArrayList<Function<WorldObject>> onStep) {
		this.onStep = onStep;
	}
	
	public void step() {
		parameterCounter = 0;
		actionType = 2;
		for (Function<WorldObject> function : onStep) {
			function.execute(this);
		}
	}
	
	public void addOnExit(String string) {
		for (String str : string.split("/")) {
			for (Function<WorldObject> function : allObjectFunctions) {
				if (function.getName().equals(str)) {
					this.onExit.add(function);
				}
			}
		}
	}
	
	public void setOnExit(ArrayList<Function<WorldObject>> onExit) {
		this.onExit = onExit;
	}
	
	public void exit() {
		parameterCounter = 0;
		actionType = 3;
		for (Function<WorldObject> function : onExit) {
			function.execute(this);
		}
	}
	
	
	//loading method
	public static void loadMapObjects(String datapath) {
		String delimeter = "\\s+";
		try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(datapath)))) {
			Pattern pattern = Pattern.compile("(\\d+)" + delimeter + "(\\d+)" + delimeter + "(\\d+)((" + delimeter + "\\d\\[.+\\])+)?");
			Matcher matcher;
			int[] args = new int[3];
			ArrayList<String> str = new ArrayList<>();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.matches("^#+.*")) { // comment line ##hey hey heys
					continue;
				}
				matcher = pattern.matcher(line);
				if (matcher.find()) {
					for (int i = 0; i < 3 ; i ++) {
						args[i] = Integer.parseInt(matcher.group(i + 1));
					}
					if (matcher.group(4) != null) {
						for (String string : matcher.group(4).trim().split(delimeter)) {
							str.add(string);
						}
					}
					createWorldObject(args[0], args[1], args[2], str);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadWorldObjects() {
		try (Scanner scanner = new Scanner(new BufferedReader(new FileReader("load\\worldobjects.txt")))) {
			Pattern pattern = Pattern.compile("(\\d+)\\s+(\\w+[/\\w+]*|-)\\s+(\\w+[/\\w+]*|-)\\s+(\\w+[/\\w+]*|-)\\s+(\\w+[/\\w+]*|-)\\s+(\\d+)\\s+(\\d+)\\s+(\\w+)\\s+(\\w+)\\s+(\\w+)\\s+(\\w+)\\s+(-?\\d+)");
			Matcher matcher;
			String[] args = new String[12];
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.matches("^#+.*")) { // comment line ##hey hey heys
					continue;
				}
				matcher = pattern.matcher(line);
				if (matcher.find()) {
					for (int i = 0; i < 12 ; i ++) {
						args[i] = matcher.group(i + 1);
					}
					allWorldObjects.add(new WorldObject(args));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadObjectImages() {
		// TODO Auto-generated method stub
		Image img = new Image(new File(DEFAULT_IMG_PATH).toURI().toString());
		try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(DEFAULT_IMGPOS_PATH)))) {
			// objectcode xpos ypos width height
			Pattern pattern = Pattern.compile("(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");
			Matcher matcher;
			int[] args = new int[5];
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.matches("^#+.*")) { // comment line ##hey hey heys
					continue;
				}
				matcher = pattern.matcher(line);
				if (matcher.find()) {
					for (int i = 0; i < 5 ; i ++) {
						args[i] = Integer.parseInt(matcher.group(i + 1));
					}
					int frame = allWorldObjects.get(args[0]).frameNumber;
					ArrayList<Image> array = new ArrayList<>();
					for (int i = 0; i < frame; i++) {
						array.add(DrawingUtility.resize(new WritableImage(img.getPixelReader(), args[1] + (args[3] / frame) * i, args[2], (args[3] / frame), args[4]), 2));
					}
					objectImagesSet.add(array);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//All Object Function
	public static void loadObjectFunctions() {
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "-";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
			}
		});

		Consumer<WorldObject> hide = object -> object.play();
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "play";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				object.show();
				object.play();
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "playback";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				object.show();
				object.setPlayback(true);
				object.play();
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "playforward";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				object.show();
				object.setPlayback(false);
				object.play();
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "delay";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				String parameter = object.functionParameter.get(object.actionType).get(object.parameterCounter);
				int delay = Integer.parseInt(parameter);
				while (delay > 0) {
					delay--;
					Clock.tick();
				}
				object.parameterCounter++;
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "hide";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				object.hide();
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "hideplayer";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				WorldManager.getPlayer().hide();
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "showplayer";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				WorldManager.getPlayer().show();
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "spawn";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				Random random = new Random();
				if (random.nextInt(100) < 8) {
					System.out.println("to fightmap !!!");
					Set<Player> players = new HashSet<Player>();
					players.add(PlayerCharacter.getMe());
					new GUIFightGameManager(players);
				}
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "changemap";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				String[] parameters = object.functionParameter.get(object.actionType).get(object.parameterCounter).split("/");
				object.parameterCounter++;
				WorldManager.changeWorld(parameters[0], Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2]));
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "playerpause";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				WorldManager.getPlayer().pause();
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "playerunpause";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				WorldManager.getPlayer().unpause();
			}
		});
		
		allObjectFunctions.add(new Function<WorldObject>() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "playerwalk";
			}
			
			@Override
			public void execute(WorldObject object) {
				// TODO Auto-generated method stub
				WorldManager.getPlayer().walk();
			}
		});
	}

}