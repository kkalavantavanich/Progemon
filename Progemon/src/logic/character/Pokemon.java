package logic.character;

import java.util.ArrayList;

import logic.terrain.FightMap;
import logic.terrain.FightTerrain;

public class Pokemon {
	
	private static enum MoveType{
		FLY, SWIM, WALK;
		
		public boolean check(FightTerrain ft){
			switch (ft.getType()){
			case WATER:
				return this.equals(SWIM) || this.equals(FLY);
			case ROCK: case TREE:
				return this.equals(FLY);
			case GRASS: default:
				return true;
			}
		}
	}
	
	private double attack, defend, moveRange, speed, hp, nextTurnTime;
	private int x, y;
	private MoveType moveType;
	private ArrayList<ActiveSkill> activeSkills = new ArrayList<ActiveSkill>();
	private ArrayList<passiveSkill> passiveSkills = new ArrayList<PassiveSkill>();
	
	public Pokemon(double attack, double defend, double moveRange, double speed, double hp, int x, int y, MoveType moveType) {
		// TODO Auto-generated constructor stub
		this.attack = attack;
		this.defend = defend;
		this.moveRange = moveRange;
		this.speed = speed;
		this.hp = hp;
		this.x = x;
		this.y = y;
		nextTurnTime = 1/speed;
		this.moveType = moveType;
	}
	
	public double getHp() {
		return hp;
	}
	
	public void setHp(double hp) {
		if(hp < 0){
			hp = 0;
		}
		else{
			this.hp = hp;
		}
	}
	
	public void move(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void attack(Pokemon p, int selectedSkill){
		p.setHp(p.getHp() - activeSkills.get(selectedSkill).getPower * attack);
	}

}
