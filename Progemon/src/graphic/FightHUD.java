package graphic;

import java.util.ArrayList;
import java.util.List;

import logic_fight.character.pokemon.Pokemon;
import logic_fight.terrain.FightMap;
import utility.InputUtility;

public class FightHUD implements IRenderable {

	private static List<Boolean> skillMenuSelect = new ArrayList<>(4);
	private static int skillMenuInterval = 15;
	private static Pokemon currentPokemon;
	private static boolean visible = true, showSkillMenu = false;

	public FightHUD() {
		for (int i = 0; i < Pokemon.MAX_ACTIVE_SKILLS; i++) {
			skillMenuSelect.add(false);
		}
		show();
	}

	public static void checkInput() {
		for (int i = 0; i < Pokemon.MAX_ACTIVE_SKILLS; i++) {
			skillMenuSelect.set(i, false);
		}
		if (currentPokemon == null) {
			return;
		}
		int blockSize = FightMap.getBlockSize();
		int x = (int) (InputUtility.getMouseX() - FightMap.getOriginX()
				- (currentPokemon.getCurrentFightTerrain().getX() + 0.5) * blockSize);
		int y = (int) (InputUtility.getMouseY() - FightMap.getOriginY() - (

		currentPokemon.getCurrentFightTerrain().getY() + 0.5) * blockSize);
		if (x < skillMenuInterval && x >= -skillMenuInterval) {
			return;
		}
		if (y < skillMenuInterval && y >= -skillMenuInterval) {
			return;
		}
		if (x < -48 - skillMenuInterval || x >= 48 + skillMenuInterval || y < -48 - skillMenuInterval
				|| y >= 48 + skillMenuInterval) {
			return;
		}
		if (x < 0 && y < 0) {
			if (InputUtility.isMouseLeftPress()) {
				skillMenuSelect.set(0, true);
			}
		} else if (x >= 0 && y < 0) {
			if (InputUtility.isMouseLeftPress()) {
				skillMenuSelect.set(1, true);
			}
		} else if (x < 0 && y >= 0) {
			if (InputUtility.isMouseLeftPress()) {
				skillMenuSelect.set(2, true);
			}
		} else if (x >= 0 && y >= 0) {
			if (InputUtility.isMouseLeftPress()) {
				skillMenuSelect.set(3, true);
			}
		}
	}

	public static void setCurrentPokemon(Pokemon currentPokemon) {
		FightHUD.currentPokemon = currentPokemon;
	}

	public static List<Boolean> getSkillMenuSelect() {
		return skillMenuSelect;
	}

	public static int getSelectedSkill() {
		for (int i = 0; i < 4; i++) {
			if (skillMenuSelect.get(i)) {
				return i;
			}
		}
		return -1;
	}

	public static int getSkillMenuInterval() {
		return skillMenuInterval;
	}

	@Override
	public void draw() {
		DrawingUtility.drawFightHUD(this);
	}

	@Override
	public int getDepth() {
		return Integer.MAX_VALUE;
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
	public void show() {
		visible = true;
		IRenderableHolder.addFightObject(this);
	}

	@Override
	public void hide() {
		visible = false;
		IRenderableHolder.removeFightObject(this);
	}

	public static boolean isShowSkillMenu() {
		return showSkillMenu;
	}

	public static void setShowSkillMenu(boolean showSkillMenu) {
		FightHUD.showSkillMenu = showSkillMenu;
	}

	public static Pokemon getCurrentPokemon() {
		return currentPokemon;
	}

}
