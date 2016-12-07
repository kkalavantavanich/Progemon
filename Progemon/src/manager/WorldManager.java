package manager;

import java.util.ArrayList;

import graphic.IRenderableHolder;
import javafx.scene.input.KeyCode;
import logic.character.PlayerCharacter;
import logic.player.HumanPlayer;
import logic.terrain.WorldMap;
import logic.terrain.WorldObject;
import utility.AnimationUtility;
import utility.Clock;
import utility.Function;
import utility.GlobalPhase;
import utility.InputUtility;

public class WorldManager {

	private static PlayerCharacter player;
	private static ArrayList<WorldObject> worldObjects = new ArrayList<>();
	private static WorldMap worldMap;
	private static WorldObject space;

	public static PlayerCharacter getPlayer() {
		return player;
	}

	public WorldManager() {

		GlobalPhase.setCurrentPhase(GlobalPhase.WORLD);
		// TODO Auto-generated constructor stub
		new AnimationUtility();
		WorldObject.loadObjectFunctions();
		WorldObject.loadWorldObjects();
		WorldObject.loadObjectImages();

		WorldMap.loadTileset();

		new Clock();
		player = new PlayerCharacter();
		/*
		 * WorldObject.loadMapObjects(
		 * "load\\worldmap\\littleroot\\littleroot_object.txt"); worldMap = new
		 * WorldMap("load\\worldmap\\littleroot\\littleroot_map.txt");
		 */
		loadWorld("littleroot", 12, 14);

		space = WorldObject.createWorldObject(0, -1, -1, new ArrayList<>());
		space.hide();
		space.addOnEnter("-");
		space.addOnExit("-");
		player.show();

		manage();
	}

	public static void manage() {
		/*
		 * player.turn(0); player.walk(); player.walk(); player.walk();
		 * player.turn(3); player.walk(); player.walk(); player.walk();
		 * player.turn(2); player.walk(); player.walk(); player.walk();
		 */
		while (true) {
			if (InputUtility.getKeyPressed(KeyCode.DOWN)) {
				if (player.getDirection() == 0) {
					if (!player.isPlaying()) {
						if (worldMap.getTerrainAt(player.getBlockX(), player.getBlockY() + 1) <= 0) {
							player.stuck();
						} else {
							player.walk();
						}
					}
				} else if (player.isMoving() && !player.isWalking()) {
					if (worldMap.getTerrainAt(player.getBlockX(), player.getBlockY() + 1) <= 0) {
						player.setDirection(0);
						player.stuck();
					} else {
						player.setDirection(0);
						player.walk();
					}
				} else if (!player.isPlaying()) {
					player.turn(0);
				}
			} else if (InputUtility.getKeyPressed(KeyCode.LEFT)) {
				if (player.getDirection() == 1) {
					if (!player.isPlaying()) {
						if (worldMap.getTerrainAt(player.getBlockX() - 1, player.getBlockY()) <= 0) {
							player.stuck();
						} else {
							player.walk();
						}
					}
				} else if (player.isMoving() && !player.isWalking()) {
					if (worldMap.getTerrainAt(player.getBlockX() - 1, player.getBlockY()) <= 0) {
						player.setDirection(1);
						player.stuck();
					} else {
						player.setDirection(1);
						player.walk();
					}
				} else if (!player.isPlaying()) {
					player.turn(1);
				}
			} else if (InputUtility.getKeyPressed(KeyCode.UP)) {
				if (player.getDirection() == 2) {
					if (!player.isPlaying()) {
						if (worldMap.getTerrainAt(player.getBlockX(), player.getBlockY() - 1) <= 0) {
							player.stuck();
						} else {
							player.walk();
						}
					}
				} else if (player.isMoving() && !player.isWalking()) {
					if (worldMap.getTerrainAt(player.getBlockX(), player.getBlockY() - 1) <= 0) {
						player.setDirection(2);
						player.stuck();
					} else {
						player.setDirection(2);
						player.walk();
					}
				} else if (!player.isPlaying()) {
					player.turn(2);
				}
			} else if (InputUtility.getKeyPressed(KeyCode.RIGHT)) {
				if (player.getDirection() == 3) {
					if (!player.isPlaying()) {
						if (worldMap.getTerrainAt(player.getBlockX() + 1, player.getBlockY()) <= 0) {
							player.stuck();
						} else {
							player.walk();
						}
					}
				} else if (player.isMoving() && !player.isWalking()) {
					if (worldMap.getTerrainAt(player.getBlockX() + 1, player.getBlockY()) <= 0) {
						player.setDirection(3);
						player.stuck();
					} else {
						player.setDirection(3);
						player.walk();
					}
				} else if (!player.isPlaying()) {
					player.turn(3);
				}
			} else if (!player.isStucking() && !player.isWalking()) {
				player.setMoving(false);
			}
			Clock.tick();
		}
	}

	public static void addWorldObjects(WorldObject worldObject) {
		worldObjects.add(worldObject);
	}

	public static WorldObject getObjectAt(int x, int y) {
		for (WorldObject worldObject : worldObjects) {
			if (worldObject.getBlockX() == x && worldObject.getBlockY() == y) {
				return worldObject;
			}
		}
		return space;
	}

	public static void loadWorld(String mapName, int playerStartX, int playerStartY) {
		IRenderableHolder.clearObjects();
		worldObjects.clear();
		worldMap = new WorldMap("load\\worldmap\\" + mapName + "\\" + mapName + "_map.txt");
		player.setX(playerStartX * 32);
		player.setY(playerStartY * 32);
		player.setBlockX(playerStartX);
		player.setBlockY(playerStartY);
		IRenderableHolder.addWorldObject(player);
		WorldObject.loadMapObjects("load\\worldmap\\" + mapName + "\\" + mapName + "_object.txt");
	}

	public static void changeWorld(String mapName, int playerStartX, int playerStartY) {
		AnimationUtility.getLoadScreen00().show();
		AnimationUtility.getLoadScreen00().play();
		while (AnimationUtility.getLoadScreen00().isPlaying()) {
			Clock.tick();
		}
		loadWorld(mapName, playerStartX, playerStartY);
		AnimationUtility.getLoadScreen00().show();
		AnimationUtility.getLoadScreen00().setPlayback(true);
		AnimationUtility.getLoadScreen00().play();
		while (AnimationUtility.getLoadScreen00().isPlaying()) {
			Clock.tick();
		}
		AnimationUtility.getLoadScreen00().setPlayback(false);
		AnimationUtility.getLoadScreen00().hide();
	}

}
