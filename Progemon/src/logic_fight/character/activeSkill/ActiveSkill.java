package logic_fight.character.activeSkill;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import graphic.Animation;
import graphic.DrawingUtility;
import graphic.IRenderable;
import javafx.scene.image.Image;
import logic_fight.character.Element;
import logic_fight.character.pokemon.Pokemon;
import logic_fight.terrain.FightTerrain;
import utility.StringUtility;

public class ActiveSkill extends Animation implements IRenderable {
	private static final double DEFAULT_POWER = 50;

	private static ArrayList<ActiveSkill> allActiveSkills = new ArrayList<ActiveSkill>();
	private static Image nullIcon;

	private String name;
	private double power;
	private FightTerrain attackTerrain, targetTerrain;
	private SkillEffect skillEffect;
	private Element element;
	private AreaType areaType;
	private GraphicType graphicType;
	private Image icon;
	private String sfxName;

	static {
		try {
			File file = new File("load\\img\\skill\\null.png");
			nullIcon = new Image(file.toURI().toString());
		} catch (NullPointerException ex) {
			System.err.println("Null ActiveSkill Icon File not found");
		}
	}

	private ActiveSkill(String skillName, double skillPower) {
		super(16, 2, false, true);
		setName(skillName);
		setPower(skillPower);
		allActiveSkills.add(this);
		loadAnimationImage("load/img/skill/Flamethrower/all.png");
		loadIcon();
	}

	@Override
	public String toString() {
		return "ActiveSkill " + name + " POWER:" + power;
	}

	public static ActiveSkill getActiveSkill(String skillName) {
		return getActiveSkill(skillName, DEFAULT_POWER, true);
	}

	public static ActiveSkill getActiveSkill(String skillName, double powerIfNotCreated) {
		return getActiveSkill(skillName, powerIfNotCreated, true);
	}

	public static ActiveSkill getActiveSkill(String skillName, double powerIfNotCreated, boolean verbose) {
		return allActiveSkills.stream().filter(e -> e.getName().equalsIgnoreCase(skillName)).findAny().orElseGet(() -> {
			if (verbose) {
				System.out.println("ActiveSkill : ActiveSkill " + skillName + " not found...");
				System.out.println("ActiveSkill : Creating new ActiveSkill with power " + powerIfNotCreated + ".");
			}
			return new ActiveSkill(skillName, powerIfNotCreated);
		});

	}

	public void setOnAttack(SkillEffect skillEffect) {
		this.skillEffect = skillEffect;
	}

	public SkillEffect getSkillEffect() {
		return skillEffect;
	}

	public void applySkillEffect(Pokemon user, Pokemon hitted) {
		skillEffect.apply(this, user, hitted);
	}

	// Array Getter
	public static List<ActiveSkill> getAllActiveSkills() {
		return Collections.unmodifiableList(allActiveSkills);
	}

	public static void clearAllActiveSkills() {
		allActiveSkills.clear();
	}

	// getters and setters
	public final double getPower() {
		return power;
	}

	public final String getName() {
		return name;
	}

	public final void setPower(double power) {
		this.power = power < 0 ? 0 : power;
	}

	private final void setName(String name) {
		if (name == null || name.matches("\\w") || name.length() <= 1) {
			name = "";
			System.err.println("ActiveSkill : Skill has no name!");
		} else {
			name = StringUtility.toTitleCase(name);
		}
		this.name = name;
	}

	@Override
	public void draw() {
		DrawingUtility.drawActiveSkill(this);
	}

	@Override
	public int getDepth() {
		return 0;
	}

	public void setAttackTerrain(FightTerrain attackTerrain) {
		this.attackTerrain = attackTerrain;
	}

	public void setTargetTerrain(FightTerrain targetTerrain) {
		this.targetTerrain = targetTerrain;
	}

	public FightTerrain getAttackTerrain() {
		return attackTerrain;
	}

	public FightTerrain getTargetTerrain() {
		return targetTerrain;
	}

	public final AreaType getAreaType() {
		return areaType;
	}

	public final GraphicType getGraphicType() {
		return graphicType;
	}

	public final void setAreaType(AreaType areaType) {
		this.areaType = areaType;
	}

	public final void setGraphicType(GraphicType graphicType) {
		this.graphicType = graphicType;
	}

	public final Element getElement() {
		return element;
	}

	public final void setElement(Element element) {
		this.element = element;
	}

	public void loadIcon() {
		try {
			File file = new File("load\\img\\skill\\" + name + "\\icon.png");
			icon = new Image(file.toURI().toString());
		} catch (NullPointerException ex) {
			System.err.println("ActiveSkill Icon not found activeSkill=" + name);
		}
	}

	public final String getSfxName() {
		return sfxName;
	}

	public final void setSfxName(String sfxName) {
		this.sfxName = sfxName;
	}

	public Image getIcon() {
		return icon;
	}

	public static Image getNullIcon() {
		return nullIcon;
	}

}
