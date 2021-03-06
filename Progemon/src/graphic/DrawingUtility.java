package graphic;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.sun.javafx.tk.Toolkit;

import item.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import logic_fight.character.activeSkill.ActiveSkill;
import logic_fight.character.pokemon.NonVolatileStatus;
import logic_fight.character.pokemon.Pokemon;
import logic_fight.player.HumanPlayer;
import logic_fight.terrain.FightMap;
import logic_fight.terrain.FightTerrain;
import logic_world.player.Character;
import logic_world.player.PlayerCharacter;
import logic_world.terrain.WorldDirection;
import logic_world.terrain.WorldMap;
import logic_world.terrain.WorldMapException;
import logic_world.terrain.WorldObject;
import manager.WorldManager;
import utility.GlobalPhase;

public class DrawingUtility {

	private static final double BLOCKS_VISIBLE_SOUTH = 6.5;
	private static final int BLOCKS_VISIBLE_EAST = 8;
	private static final double BLOCKS_VISIBLE_NORTH = 5.5;
	private static final int BLOCKS_VISIBLE_WEST = 7;

	private static final int QUEUE_BOX_HEIGHT = 202;
	private static final int QUEUE_BOX_WIDTH = 68;
	private static final Color QUEUE_BOX_TEXT_COLOR = Color.BLACK;
	// private static final int ITEM_ICON_X = 10;
	// private static final int ITEM_ICON_Y = 246;
	private static final Color FIGHT_MAP_FRAME_COLOR = Color.BLACK;
	private static final Color EMPTY_EXP_BAR_HUD_COLOR = Color.BLACK;
	private static final Color EMPTY_HP_BAR_HUD_COLOR = Color.BLACK;
	private static final Color EXP_COLOR = Color.AQUA;

	private static final int HP_BAR_SIZE_X = 30;
	private static final int HP_BAR_SIZE_Y = 6;
	private static final int HP_BAR_OFFSET_X = 5;
	private static final int HP_BAR_OFFSET_Y = 32;
	private static final Color HP_GREEN = Color.LAWNGREEN;
	private static final Color HP_RED = Color.RED;

	private static Image shadow;
	private static Image cursor;
	private static Image highlight;

	private static Image queueBoxImage;
	private static Image sign;
	private static Image pkmnBar;
	private static Image background;
	private static Image itemLabel;
	private static GraphicsContext gc;

	private static double playerX, playerY;

	public DrawingUtility() {
		try {
			// File sfile = new File("load\\img\\terrain\\shadow20.png");
			shadow = new Image(ClassLoader.getSystemResource("img/terrain/shadow20.png").toString());
			// File cfile = new File("load\\img\\terrain\\cursur.png");
			cursor = new Image(ClassLoader.getSystemResource("img/terrain/cursur.png").toString());
			// File hfile = new File("load\\img\\terrain\\highlight.png");
			highlight = new Image(ClassLoader.getSystemResource("img/terrain/highlight.png").toString());
			File qfile = new File(QueueBox.QUEUE_BOX_PATH);
			queueBoxImage = resize(new Image(qfile.toURI().toString()), 2);
			File signfile = new File("load\\img\\dialogbox\\Theme1_sign.gif");
			sign = new Image(signfile.toURI().toString());
			// File pkmnfile = new File("load\\img\\HUD\\pokemonbar.png");
			pkmnBar = new Image(ClassLoader.getSystemResource("img/HUD/pokemonbar.png").toString());
			// File backgroundfile = new
			// File("load\\img\\background\\meadow.png");
			background = new Image(ClassLoader.getSystemResource("img/background/meadow.png").toString()
					);
			// File itemlabelfile = new File("load\\img\\HUD\\itemlabel.png");
			itemLabel = new Image(ClassLoader.getSystemResource("img/HUD/itemlabel.png").toString());
			System.out.println("Drawing Utility Loaded Successfully.");
		} catch (IllegalArgumentException ex) {
			System.err.println("DrawingUtitity cannot load static files");
			ex.printStackTrace();
		}

	}

	public static void drawFightMap(FightMap fightMap) {
		// for (FightTerrain[] fightTerrains : fightMap.getMap()) {
		// for (FightTerrain fightTerrain : fightTerrains) {
		// fightTerrain.draw();
		// }
		// }
		gc.drawImage(background, 0, 0);
		gc.setFill(FIGHT_MAP_FRAME_COLOR);
		gc.setLineWidth(3);
		gc.strokeRect(FightMap.getOriginX(), FightMap.getOriginY(), fightMap.getSizeX() * FightMap.getBlockSize(),
				fightMap.getSizeY() * FightMap.getBlockSize());
		Arrays.asList(fightMap.getMap()).stream().flatMap((FightTerrain[] ft) -> Arrays.asList(ft).stream())
				.forEach(ft -> ft.draw());
		fightMap.getPokemonsOnMap().forEach(p -> p.draw());
		// for (int i = 0; i < fightMap.getPokemonsOnMap().size(); i++) {
		// fightMap.getPokemonsOnMap().get(i).draw();
		// }
		fightMap.getPokemonsOnMap().forEach(p -> drawPokemonBar(p));
	}

	public static void drawFightTerrain(FightTerrain fightTerrain) {
		int blockSize = FightMap.getBlockSize();
		int fightTerrainX = fightTerrain.getX() * blockSize + FightMap.getOriginX();
		int fightTerrainY = fightTerrain.getY() * blockSize + FightMap.getOriginY();

		gc.drawImage(fightTerrain.getTerrainImage(), fightTerrainX, fightTerrainY, blockSize, blockSize);
		if (fightTerrain.isShadowed()) {
			gc.drawImage(shadow, fightTerrainX, fightTerrainY, blockSize, blockSize);
		}
		if (fightTerrain.isCursor()) {
			gc.drawImage(cursor, fightTerrainX, fightTerrainY, blockSize, blockSize);
			fightTerrain.setCursor(false);
		}
		if (fightTerrain.isHighlight()) {
			gc.drawImage(highlight, fightTerrainX, fightTerrainY, blockSize, blockSize);
		}
	}

	public static void drawPokemon(Pokemon pokemon) {
		if (pokemon.getCurrentFightTerrain() == null) {
			return;
		}
		int blockSize = FightMap.getBlockSize();
		double resizeRate = blockSize / 40.0;
		int x = pokemon.getCurrentFightTerrain().getX() * blockSize + FightMap.getOriginX();
		int y = pokemon.getCurrentFightTerrain().getY() * blockSize + FightMap.getOriginY();
		if (pokemon.isVisible()) {
			gc.drawImage(pokemon.getImage(), x, y, blockSize, blockSize);
		}
		/*
		 * Image img = new ImageIcon(pokemon.getImageName()).getImage();
		 * gc.drawImage(img, pokemon.getX() * 40, pokemon.getY() * 40, 40, 40,
		 * null);
		 */
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		gc.strokeRect(x + HP_BAR_OFFSET_X * resizeRate, y + HP_BAR_OFFSET_Y * resizeRate, HP_BAR_SIZE_X * resizeRate,
				HP_BAR_SIZE_Y * resizeRate);
		gc.setFill(HP_RED);
		gc.fillRect(x + HP_BAR_OFFSET_X * resizeRate, y + HP_BAR_OFFSET_Y * resizeRate, HP_BAR_SIZE_X * resizeRate,
				HP_BAR_SIZE_Y * resizeRate);
		gc.setFill(HP_GREEN);
		gc.fillRect(x + HP_BAR_OFFSET_X * resizeRate, y + HP_BAR_OFFSET_Y * resizeRate,
				(int) (pokemon.getCurrentHP() * HP_BAR_SIZE_X * resizeRate / pokemon.getFullHP()),
				HP_BAR_SIZE_Y * resizeRate);

		if (pokemon.getStatus().equals(NonVolatileStatus.FREEZE)) {
			gc.setGlobalAlpha(0.4);
			gc.setFill(Color.ALICEBLUE);
			gc.fillRect(x, y, FightTerrain.IMG_SIZE_X, FightTerrain.IMG_SIZE_Y);
			gc.setGlobalAlpha(1.0);
		}
	}

	public static void drawPokemonBar(Pokemon pokemon) {
		if (pokemon == null || pokemon.getCurrentFightMap() == null) {
			return;
		}
		int position;
		int originX, originY;
		gc.setFont(Font.font("monospace", 8));
		if (pokemon.getOwner() instanceof HumanPlayer) {
			position = pokemon.getCurrentFightMap().getPlayerPokemonsOnMap().indexOf(pokemon);
			originX = 110 + position * 120;
			originY = 249;
		} else {
			position = pokemon.getCurrentFightMap().getEnemyPokemonsOnMap().indexOf(pokemon);
			originX = (10 + position * 120);
			originY = 10;
		}
		gc.setFill(EMPTY_HP_BAR_HUD_COLOR);
		gc.fillRect(originX + 42, originY + 27, 60, 4);
		gc.setFill(Color.hsb((pokemon.getCurrentHP() / pokemon.getFullHP()) * 120, 1,
				1 - (pokemon.getCurrentHP() / pokemon.getFullHP()) * 0.2));
		gc.fillRect(originX + 42, originY + 27, (pokemon.getCurrentHP() / pokemon.getFullHP()) * 60, 4);
		gc.drawImage(pkmnBar, originX, originY);
		gc.drawImage(pokemon.getIconImage(), originX, originY);

		gc.setFill(EMPTY_EXP_BAR_HUD_COLOR);
		gc.fillText(pokemon.getName(), originX + 42, originY + 10);
		gc.fillText("Lv" + pokemon.getLevel(), originX + 44, originY + 23);
		gc.fillText((int) pokemon.getCurrentHP() + "/" + (int) pokemon.getFullHP(), originX + 98
				- computeStringWidth((int) pokemon.getCurrentHP() + "/" + (int) pokemon.getFullHP(), gc.getFont()),
				originY + 23);
		gc.setFill(EXP_COLOR);
		gc.fillRect(originX + 33, originY + 37, ((pokemon.getCurrentExp() - pokemon.getLastExpRequired())
				/ (pokemon.getNextExpRequired() - pokemon.getLastExpRequired())) * 77, 1);
	}

	public static void drawFightHUD(FightHUD fightHUD) {
		if (!FightHUD.isShowSkillMenu()) {
			return;
		}
		int interval = FightHUD.getSkillMenuInterval();
		Pokemon pokemon = FightHUD.getCurrentPokemon();
		int i = 0;
		int blockSize = FightMap.getBlockSize();
		int x = (int) ((pokemon.getCurrentFightTerrain().getX() + 0.5) * blockSize + FightMap.getOriginX());
		int y = (int) ((pokemon.getCurrentFightTerrain().getY() + 0.5) * blockSize + FightMap.getOriginY());
		for (ActiveSkill activeSkill : pokemon.getActiveSkills()) {
			if (activeSkill.getIcon().getWidth() > 0) {
				gc.drawImage(activeSkill.getIcon(), x - 48 - interval + (i % 2) * (48 + interval * 2),
						y - 48 - interval + Math.floorDiv(i, 2) * (48 + interval * 2));
				if (FightHUD.getSelectedSkill() == i) {
					gc.save();
					gc.setFill(Color.BLACK);
					gc.setGlobalAlpha(0.5);
					gc.fillRect(x - 48 - interval + (i % 2) * (48 + interval * 2) + 4,
							y - 48 - interval + Math.floorDiv(i, 2) * (48 + interval * 2) + 4, 40, 40);
					gc.restore();
				}
			} else {
				gc.setFill(Color.WHITE);
				gc.fillRect(x - 48 - interval + (i % 2) * (48 + interval * 2),
						y - 48 - interval + Math.floorDiv(i, 2) * (48 + interval * 2), 48, 48);
			}
			i++;
		}
		for (; i < 4; i++) {
			gc.drawImage(ActiveSkill.getNullIcon(), x - 48 - interval + (i % 2) * (48 + interval * 2),
					y - 48 - interval + Math.floorDiv(i, 2) * (48 + interval * 2));
		}
	}

	public static void drawDialogBox() {
		/*
		 * File signfile = new File("load\\img\\dialogbox\\Theme1_sign.gif");
		 * Image sign = new Image(signfile.toURI().toString());
		 */
		gc.save();
		gc.beginPath();
		gc.rect(0, DialogBox.instance.getY() + 10, GameScreen.WIDTH, 64);
		if (GlobalPhase.getCurrentPhase() == GlobalPhase.FIGHT) {
			gc.setFill(Color.NAVY.darker());
			gc.fillRect(0, 288, 480, 96);
		}
		gc.drawImage(DialogBox.instance.getDialogBoxImage(), DialogBox.instance.getX(), DialogBox.instance.getY());

		gc.clip();
		gc.setFill(DialogBox.FONT_COLOR);
		gc.setFont(DialogBox.instance.getFont());
		double messageHeight = new Text("Test").getLayoutBounds().getHeight();
		gc.fillText(DialogBox.instance.getMessageOnScreen()[0], DialogBox.instance.getX() + 25,
				DialogBox.instance.getY() + 15 + messageHeight - DialogBox.instance.getyShift());
		gc.fillText(DialogBox.instance.getMessageOnScreen()[1], DialogBox.instance.getX() + 25,
				DialogBox.instance.getY() + 45 + messageHeight - DialogBox.instance.getyShift());

		if (DialogBox.instance.isShowSign()) {
			gc.drawImage(sign, DialogBox.instance.getX() + 25 + DialogBox.instance.getEndLineWidth(),
					DialogBox.instance.getY() + DialogBox.instance.getCurrentLine() * 25 + 14);

		}
		gc.restore();
	}

	public static void drawQueueBox() {
		/*
		 * File qfile = new File(QueueBox.QUEUE_BOX_PATH); Image qimg = new
		 * Image(qfile.toURI().toString());
		 */
		Image img;
		gc.drawImage(queueBoxImage, QueueBox.BOX_X, QueueBox.BOX_Y);

		gc.save();
		gc.beginPath();
		gc.rect(QueueBox.ORIGIN_X, QueueBox.ORIGIN_Y, QUEUE_BOX_WIDTH, QUEUE_BOX_HEIGHT);
		gc.clip();
		gc.closePath();

		List<Pokemon> pokemonsOnQueue = QueueBox.getPokemonsOnQueue();
		for (int i = 0; i < pokemonsOnQueue.size(); i++) {
			img = pokemonsOnQueue.get(i).getImage();
			gc.setFill(pokemonsOnQueue.get(i).getOwner().getColor());
			gc.fillRect(QueueBox.ORIGIN_X + QueueBox.getDelta()[i][0] + 6,
					QueueBox.ORIGIN_Y + QueueBox.getDelta()[i][1] + 2 + i * 40, 6, 36);

			gc.setFill(QUEUE_BOX_TEXT_COLOR);
			gc.setFont(DialogBox.instance.getFont());

			gc.setFill(Color.BLACK);
			gc.setFont(DialogBox.instance.getFont());
			double messageHeight = new Text("LV.").getLayoutBounds().getHeight();
			gc.fillText("Lv." + pokemonsOnQueue.get(i).getLevel(), QueueBox.ORIGIN_X + QueueBox.getDelta()[i][0] + 24,
					QueueBox.ORIGIN_Y + QueueBox.getDelta()[i][1] + 15 + i * 40 + messageHeight);

			gc.drawImage(img, QueueBox.ORIGIN_X + QueueBox.getDelta()[i][0],
					QueueBox.ORIGIN_Y + QueueBox.getDelta()[i][1] + i * 40);
		}
		gc.restore();

	}

	public static void drawItemBox(ItemBox itembox) {
		gc.drawImage(itembox.getItemButtonImage(), 0, 248);
		gc.drawImage(itembox.getRunButtonImage(), 50, 248);
		if (itembox.isVisible()) {
			gc.drawImage(itembox.getTabImage(), ItemBox.X, ItemBox.Y);
			gc.save();
			gc.beginPath();
			gc.rect(5, 90, 90, 109);
			gc.clip();
			gc.closePath();
			int i = 0;
			int labelOffset = itembox.getLabelOffset();
			int startLabel = Math.floorDiv(labelOffset, 27);
			if (itembox.getCurrentTab() == 0) {
				for (Item item : itembox.getBag().getPokeballs().keySet()) {
					if (i >= startLabel && i <= startLabel + 4) {
						gc.drawImage(itemLabel, 5, 90 + (i * 27) - labelOffset);
						gc.drawImage(item.getIcon(), 6, 90 + (i * 27) + 2 - labelOffset);
						gc.setFont(itembox.getFont());
						gc.setFill(Color.BLACK);
						gc.fillText(item.getName(), 32, 90 + (i * 27) + 17 - labelOffset);
						gc.fillText("x" + itembox.getBag().getItems().get(item), 75, 90 + (i * 27) + 17 - labelOffset);
					}
					i++;
				}
			} else {
				for (Item item : itembox.getBag().getNonPokeballs().keySet()) {
					if (i >= startLabel && i <= startLabel + 4) {
						gc.drawImage(itemLabel, 5, 90 + (i * 27) - labelOffset);
						gc.drawImage(item.getIcon(), 6, 90 + (i * 27) + 2 - labelOffset);
						gc.setFont(itembox.getFont());
						gc.setFill(Color.BLACK);
						gc.fillText(item.getName(), 32, 90 + (i * 27) + 17 - labelOffset);
						gc.fillText("x" + itembox.getBag().getItems().get(item), 78, 90 + (i * 27) + 17 - labelOffset);

					}
					i++;
				}
			}
			gc.restore();
			if (itembox.isShowScrollBar()) {
				gc.save();
				gc.setGlobalAlpha(0.5);
				gc.setFill(Color.DARKGOLDENROD.darker());
				gc.fillRect(90, 90, 5, 109);
				gc.restore();
				gc.setFill(Color.GOLD);
				gc.fillRect(90, 90 + itembox.getScrollBarY(), 5, itembox.getScrollBarSize());
			}
		}
	}

	public static void drawActiveSkill(ActiveSkill skill) {
		// Type = Line

		int blockSize = FightMap.getBlockSize();
		int x = FightMap.getOriginX();
		int y = FightMap.getOriginY();
		int fromX = skill.getAttackTerrain().getX();
		int fromY = skill.getAttackTerrain().getY();
		int toX = skill.getTargetTerrain().getX();
		int toY = skill.getTargetTerrain().getY();
		Image skillImage = skill.getCurrentImage();

		gc.save();
		gc.translate((fromX + 0.5) * blockSize + x, (fromY + 0.5) * blockSize + y);

		double distance = Math.sqrt((fromX - toX) * (fromX - toX) + (fromY - toY) * (fromY - toY));
		double angle = 0;
		angle = Math.acos((toX - fromX) / distance) * (180 / Math.PI);
		if (fromY - toY < 0) {
			angle = -angle;
		}
		if (fromX - toX > 0) {
			skillImage = verticalFlip(skillImage);
		}

		gc.rotate(-angle);

		gc.drawImage(skillImage, -blockSize / 2, -blockSize / 2, (distance + 1) * blockSize, blockSize);

		gc.restore();
	}

	public static void drawWorldMap(WorldMap worldMap) {
		playerX = PlayerCharacter.instance.getX();
		playerY = PlayerCharacter.instance.getY();
		/*
		 * xoffset = x - (blocksize * 7) yoffset = y - (blocksize * 5.5)
		 */
		double xOffset = playerX - (32 * BLOCKS_VISIBLE_WEST);
		double yOffset = playerY - (32 * BLOCKS_VISIBLE_NORTH);
		/*
		 * startBlockX = Math.floor(xoffset/blocksize) endBlockX = startBlockX +
		 * 16 startBlockY = Math.floor(yoffset/blocksize) endBlockY =
		 * startBlockX + 13
		 */
		int startBlockX = (int) Math.floor(xOffset / 32);
		int endBlockX = (int) Math.floor((playerX + (32 * BLOCKS_VISIBLE_EAST) - 1) / 32);
		int startBlockY = (int) Math.floor(yOffset / 32);
		int endBlockY = (int) Math.floor((playerY + (32 * BLOCKS_VISIBLE_SOUTH) - 1) / 32);

		int tileCode;
		for (int i = startBlockY; i <= endBlockY; i++) {
			for (int j = startBlockX; j <= endBlockX; j++) {
				try {
					tileCode = getTileCode(worldMap, i, j);
					if (tileCode != 0) {
						gc.drawImage(WorldMap.getImage(Math.abs(tileCode)), j * 32 - xOffset, i * 32 - yOffset, 32, 32);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	private static int getTileCode(WorldMap worldMap, int i, int j) throws WorldMapException {
		int tileCode;
		int mapOffset;
		int mapTrim;
		WorldMap worldToDraw;
		if (i < 0 && (worldToDraw = WorldManager.getNextWorldMaps(WorldDirection.NORTH)) != null) {
			mapOffset = Integer.parseInt(worldMap.getMapProperties().getProperty("north_offset", "0"));
			mapTrim = Integer.parseInt(worldMap.getMapProperties().getProperty("north_trim", "0"));
			tileCode = worldToDraw.getTerrainAt(j - mapOffset, i + worldToDraw.getHeight() - mapTrim);
		} else if (i >= worldMap.getHeight()
				&& (worldToDraw = WorldManager.getNextWorldMaps(WorldDirection.SOUTH)) != null) {

			mapOffset = Integer.parseInt(worldMap.getMapProperties().getProperty("south_offset", "0"));
			mapTrim = Integer.parseInt(worldMap.getMapProperties().getProperty("south_trim", "0"));
			tileCode = worldToDraw.getTerrainAt(j - mapOffset, i - worldMap.getHeight() + mapTrim);
		} else if (j < 0 && (worldToDraw = WorldManager.getNextWorldMaps(WorldDirection.WEST)) != null) {
			mapOffset = Integer.parseInt(worldMap.getMapProperties().getProperty("west_offset", "0"));
			mapTrim = Integer.parseInt(worldMap.getMapProperties().getProperty("west_trim", "0"));
			tileCode = worldToDraw.getTerrainAt(j + worldToDraw.getWidth() - mapTrim, i - mapOffset);
		} else if (j >= worldMap.getWidth()
				&& (worldToDraw = WorldManager.getNextWorldMaps(WorldDirection.EAST)) != null) {
			mapOffset = Integer.parseInt(worldMap.getMapProperties().getProperty("east_offset", "0"));
			mapTrim = Integer.parseInt(worldMap.getMapProperties().getProperty("east_trim", "0"));
			tileCode = worldToDraw.getTerrainAt(j - worldMap.getWidth() + mapTrim, i - mapOffset);
		} else if (i < 0 || j < 0 || i >= worldMap.getHeight() || j >= worldMap.getWidth()) {
			tileCode = 0;
		} else {
			tileCode = worldMap.getTerrainAt(j, i);
		}
		return tileCode;
	}

	public static void drawPlayer(PlayerCharacter player) {
		// x = blocksize * 7, y = blocksize * 5.5 - (6/16 * blocksize)
		if (player.isJumping()) {
			gc.drawImage(player.getCurrentImage(), 224, 164 + player.getyOffset(), 32, 44);
			return;
		}
		gc.drawImage(player.getCurrentImage(), 224, 164, 32, 44);
		// gc.drawImage(WorldObject.objectImagesSet.get("008").get(0), 200,
		// 200);
	}

	public static void drawCharacter(Character character) {
		try {
			int objectImageHeight = 44;
			int objectImageWidth = 32;
			int blockX = character.getBlockX();
			int blockY = character.getBlockY();
			double xOffset = playerX - (32 * BLOCKS_VISIBLE_WEST);
			double yOffset = playerY - (32 * BLOCKS_VISIBLE_NORTH);
			int startBlockX = (int) Math.floor(xOffset / 32);
			int endBlockX = (int) Math.floor((playerX + (32 * BLOCKS_VISIBLE_EAST) - 1) / 32);
			int startBlockY = (int) Math.floor(yOffset / 32);
			int endBlockY = (int) Math.floor((playerY + (32 * BLOCKS_VISIBLE_SOUTH) - 1) / 32);
			if (blockX > endBlockX || blockY < startBlockY) {
				return;
			} else if (blockX * 32 + objectImageWidth > xOffset
					|| (blockY + 1) * 32 - objectImageHeight < yOffset + 384) {
				if (character.isJumping()) {
					gc.drawImage(character.getCurrentImage(), character.getX() - xOffset,
							character.getY() + 32 - objectImageHeight - yOffset + character.getyOffset(),
							objectImageWidth, objectImageHeight);
					return;
				}
				gc.drawImage(character.getCurrentImage(), character.getX() - xOffset,
						character.getY() + 32 - objectImageHeight - yOffset, objectImageWidth, objectImageHeight);
			}
		} catch (Exception e) {
			System.err.print("DU.");
		}
	}

	public static void drawWorldObject(WorldObject worldObject) {
		try {
			int objectImageHeight = (int) worldObject.getCurrentImage().getHeight();
			int objectImageWidth = (int) worldObject.getCurrentImage().getWidth();
			int blockX = worldObject.getBlockX();
			int blockY = worldObject.getBlockY();
			double xOffset = playerX - (32 * BLOCKS_VISIBLE_WEST);
			double yOffset = playerY - (32 * BLOCKS_VISIBLE_NORTH);
			int startBlockX = (int) Math.floor(xOffset / 32);
			int endBlockX = (int) Math.floor((playerX + (32 * BLOCKS_VISIBLE_EAST) - 1) / 32);
			int startBlockY = (int) Math.floor(yOffset / 32);
			int endBlockY = (int) Math.floor((playerY + (32 * BLOCKS_VISIBLE_SOUTH) - 1) / 32);
			if (blockX > endBlockX || blockY < startBlockY) {
				return;
			} else if (blockX * 32 + objectImageWidth > xOffset
					|| (blockY + 1) * 32 - objectImageHeight < yOffset + 384) {
				gc.drawImage(worldObject.getCurrentImage(), blockX * 32 - xOffset,
						(blockY + 1) * 32 - objectImageHeight - yOffset, objectImageWidth, objectImageHeight);
			}
		} catch (Exception e) {
			System.err.print("DU.");
		}
	}

	public static void drawScreenEffect(ScreenEffect screenEffect) {
		gc.drawImage(screenEffect.getCurrentImage(), 0, 0);
	}

	public static double computeStringWidth(String text, Font font) {
		if (text.length() == 0 || gc == null) {
			return 0;
		} else {
			return Toolkit.getToolkit().getFontLoader().computeStringWidth(text, font);
		}
	}

	public static double computeStringHeight(String text, Font font) {
		if (text.length() == 0 || gc == null) {
			return 0;
		} else {
			return Toolkit.getToolkit().getFontLoader().getFontMetrics(font).getLineHeight();
		}
	}

	public static void setGC(GraphicsContext gc) {
		DrawingUtility.gc = gc;
	}

	public static GraphicsContext getGC() {
		return gc;
	}

	public static Image verticalFlip(Image image) {
		int height = (int) image.getHeight();
		int width = (int) image.getWidth();
		PixelReader pixelReader = image.getPixelReader();
		WritableImage wimg = new WritableImage(width, height);
		PixelWriter pixelWriter = wimg.getPixelWriter();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelWriter.setColor(x, height - 1 - y, pixelReader.getColor(x, y));
			}
		}
		return wimg;
	}

	public static Image horizontalFlip(Image img) {
		int height = (int) img.getHeight();
		int width = (int) img.getWidth();
		PixelReader pixelReader = img.getPixelReader();
		WritableImage wimg = new WritableImage(width, height);
		PixelWriter pixelWriter = wimg.getPixelWriter();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelWriter.setColor(width - 1 - x, y, pixelReader.getColor(x, y));
			}
		}
		return wimg;
	}

	public static Image resize(Image img, int scaleFactor) {
		int height = (int) img.getHeight() * scaleFactor;
		int width = (int) img.getWidth() * scaleFactor;
		PixelReader pixelReader = img.getPixelReader();
		WritableImage wimg = new WritableImage(width, height);
		PixelWriter pixelWriter = wimg.getPixelWriter();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelWriter.setColor(x, y,
						pixelReader.getColor((int) Math.floor(x / scaleFactor), (int) Math.floor(y / scaleFactor)));
			}
		}
		return wimg;
	}

}