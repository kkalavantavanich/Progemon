package logic_fight.player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import graphic.FightHUD;
import graphic.ItemBox;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import logic_fight.FightPhase;
import logic_fight.character.activeSkill.ActiveSkill;
import logic_fight.character.pokemon.Pokemon;
import logic_fight.terrain.FightMap;
import logic_fight.terrain.FightTerrain;
import logic_world.player.PlayerCharacter;
import utility.InputUtility;
import utility.exception.AbnormalPhaseOrderException;

public class HumanPlayer extends Player {

	Set<KeyCode> attackingKeys = new HashSet<KeyCode>(
			Arrays.asList(KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4));

	public HumanPlayer(String name)	{
		super(name);
	}

	public HumanPlayer(String name, Pokemon starter_pokemon, Color blue) {
		super(name, starter_pokemon, blue);
	}

	public HumanPlayer(String name, Color color) {
		super(name, color);
	}

	@Override
	protected boolean inputNextPath(Pokemon pokemon) throws AbnormalPhaseOrderException {
		KeyCode captureKey = KeyCode.C;
		KeyCode itemKey = KeyCode.I;
		if (InputUtility.getKeyTriggered(captureKey) && getCurrentFightManager().canCapturePokemon()) {
			throw new AbnormalPhaseOrderException(FightPhase.preCapturePhase);
		} else if (ItemBox.instance.isVisible()) {
			throw new AbnormalPhaseOrderException(FightPhase.preItemPhase);
		} else if (ItemBox.instance.getRunAndSetFalse()) {
			throw new AbnormalPhaseOrderException(FightPhase.runPhase);
		}

		if (!InputUtility.isMouseLeftClick()) {
			return false;
		} else {
			FightTerrain destination = pokemon.getCurrentFightMap().getFightTerrainAt(FightMap.getCursorX(),
					FightMap.getCursorY());
			System.out.println("Destination : " + destination);
			if (pokemon.getAvaliableFightTerrains().contains(destination)) {
				super.nextPath = pokemon.findPathTo(destination);
				return true;
			} else {	
				super.nextPath = null;
				return false;
			}
		}
	}

	@Override
	protected boolean inputAttackPokemon(Pokemon pokemon) {
		if (super.nextAttackedPokemon.isPresent()) {
			return true;
		}
		if (!InputUtility.isMouseLeftClick()) {
			return false;
		} else {
			FightTerrain destination = pokemon.getCurrentFightMap().getFightTerrainAt(FightMap.getCursorX(),
					FightMap.getCursorY());
			if (destination == null) {
				return false;
			}
			Optional<Pokemon> otherPokemon = pokemon.getCurrentFightMap().getPokemonAt(destination);
			if (otherPokemon.isPresent() && otherPokemon.get().getOwner() != pokemon.getOwner()) {
				super.nextAttackedPokemon = otherPokemon;
				if (!super.nextAttackSkill.isPresent()) {
					FightHUD.setShowSkillMenu(true);
				}
				return true;
			} else if (otherPokemon.orElse(null) == pokemon) {
				if (!super.nextAttackSkill.isPresent()) {
					FightHUD.setShowSkillMenu(true);
				}
				return false;
			} else {
				super.nextAttackedPokemon = Optional.empty();
				return false;
			}
		}
	}

	@Override
	protected boolean inputAttackActiveSkill(Pokemon attackingPokemon) throws AbnormalPhaseOrderException {
		KeyCode endTurn = KeyCode.E;
		if (InputUtility.getKeyTriggered(endTurn)) {
			FightHUD.setShowSkillMenu(false);
			throw new AbnormalPhaseOrderException(FightPhase.postAttackPhase);
		}

		if (super.nextAttackSkill.isPresent()) {
			FightHUD.setShowSkillMenu(false);
			return true;
		}
		List<ActiveSkill> attackSkills = attackingPokemon.getActiveSkills();
		for (KeyCode kc : attackingKeys) {
			int index = kc.ordinal() - KeyCode.DIGIT1.ordinal();
			if (InputUtility.getKeyTriggered(kc) && index >= 0 && index < attackSkills.size()) {
				System.out.println("Attack Skill setted");
				super.nextAttackSkill = Optional.of(attackSkills.get(index));
				FightHUD.setShowSkillMenu(false);
				return true;
			}
		}
		if (FightHUD.getSelectedSkill() >= 0 && FightHUD.getSelectedSkill() < attackSkills.size()) {
			System.out.println("Attack Skill setted");
			super.nextAttackSkill = Optional.of(attackSkills.get(FightHUD.getSelectedSkill()));
			FightHUD.setShowSkillMenu(false);
			return true;
		}
		return false;
	}

	@Override
	protected boolean inputCapturePokemon(Pokemon pokemon) throws AbnormalPhaseOrderException {
		KeyCode goBackKey = KeyCode.C;
		if (InputUtility.getKeyTriggered(goBackKey)) {
			throw new AbnormalPhaseOrderException(FightPhase.preMovePhase);
		}
		if (!InputUtility.isMouseLeftClick()) {
			return false;
		}
		FightTerrain destination = pokemon.getCurrentFightMap().getFightTerrainAt(FightMap.getCursorX(),
				FightMap.getCursorY());
		Optional<Pokemon> otherPokemon = pokemon.getCurrentFightMap().getPokemonAt(destination);
		otherPokemon.ifPresent(other -> {
			if (!other.getOwner().equals(pokemon.getOwner())) {
				captureTarget = other;
			}
		});
		System.out.println("capture target = " + captureTarget);
		return captureTarget != null;
	}

	@Override
	protected boolean inputUseItem(Pokemon pokemon) throws AbnormalPhaseOrderException {
		if (!ItemBox.instance.isVisible()) {
			throw new AbnormalPhaseOrderException(FightPhase.preMovePhase);
		}
		if (ItemBox.instance.getItemBoxInput() != null) {
			itemToUse = PlayerCharacter.instance.getBag().getAndRemove(ItemBox.instance.getItemBoxInput());
			return true;
		}
		return false;
	}

	@Override
	protected boolean inputUseItemPokemon(Pokemon pokemon) throws AbnormalPhaseOrderException {
		if (!InputUtility.isMouseLeftClick()) {
			return false;
		}
		FightTerrain destination = pokemon.getCurrentFightMap().getFightTerrainAt(FightMap.getCursorX(),
				FightMap.getCursorY());
		Optional<Pokemon> otherPokemon = pokemon.getCurrentFightMap().getPokemonAt(destination);
		otherPokemon.ifPresent(other -> {
			if (other.getOwner().equals(pokemon.getOwner())) {
				itemTargetPokemon = other;
			}
		});
		return itemTargetPokemon != null;
	}

}
