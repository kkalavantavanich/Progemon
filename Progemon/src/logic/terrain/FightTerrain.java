package logic.terrain;

import java.util.ArrayList;

/** FightTerrain */
@SuppressWarnings("unused")
public class FightTerrain {
	private int x, y;
	private boolean isShadowed;
	private TerrainType type;

	public static enum TerrainType {
		GRASS, ROCK, WATER, TREE;

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
			return this.toString() + ".png";
		}
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

}
