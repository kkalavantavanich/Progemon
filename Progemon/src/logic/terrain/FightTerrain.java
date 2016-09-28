package logic.terrain;

import java.util.ArrayList;

import graphic.FightTerrainComponent;
import graphic.Screen;

/** FightTerrain */
@SuppressWarnings("unused")
public class FightTerrain {
	private int x, y;
	private boolean isShadowed;
	private TerrainType type;

	public static enum TerrainType {
		GRASS, ROCK, WATER, TREE, GROUND;

		private int moveCost;

		private TerrainType(int moveCost) {
			this.moveCost = moveCost;
		}

		private TerrainType() {
			this(1);
		}

		public int getMoveCost() {
			return this.moveCost;
		}
		
		public String getImageName(){
			return "load\\img\\" + this.toString() + ".png";
		}
	}
	
	public FightTerrain(int x, int y, TerrainType type) {
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y = y;
		this.type = type;
	}

	private ArrayList<FightTerrain> toArrayList() {
		ArrayList<FightTerrain> temp = new ArrayList<FightTerrain>();
		temp.add(this);
		return temp;
	}

	@Override
	public String toString() {
		return "FightTerrain [x=" + x + ", y=" + y + ", isShadowed=" + isShadowed + ", type=" + type + "]";
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public final boolean isShadowed() {
		return isShadowed;
	}

	public final TerrainType getType() {
		return type;
	}
	
	public static TerrainType toFightTerrainType(String fightTerrainString){
		for(TerrainType tt : TerrainType.values()){
			if(fightTerrainString.equalsIgnoreCase(tt.toString())){
				return tt;
			}
		}
		return null;
	}
	
	public void draw(){
		Screen.getScreen().add(new FightTerrainComponent(this));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isShadowed ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FightTerrain other = (FightTerrain) obj;
		if (isShadowed != other.isShadowed)
			return false;
		if (type != other.type)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	

}
