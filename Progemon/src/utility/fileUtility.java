package utility;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import logic.character.Pokemon;
import logic.terrain.FightTerrain;
import logic.terrain.FightTerrain.TerrainType;

public class fileUtility {

	private static final String DEFAULT_PATH = "load";
	private static final String DEFAULT_LOAD_POKEMON = DEFAULT_PATH + "/pokemon_list.txt";
	private static final String DEFAULT_LOAD_POKEDEX = DEFAULT_PATH + "/pokedex.txt";
	private static final String DEFAULT_LOAD_FIGHT_MAP = DEFAULT_PATH + "/fight_map.txt";
	private static FileReader reader;
	private static FileWriter writer;
	private static Scanner scanner;

	/** Usage name attack defense moveRange speed hp moveType 
	 * @throws IOException */
	public static void loadPokemon(String filePath) throws IOException {
		try {
			reader = new FileReader(filePath);
			scanner = new Scanner(reader);
			String line;
			String[] args;
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				args = line.split(" ");
				if (args.length != 7) {
					System.out.println("Needs 7 parameters per pokemon!");
					break;
				}
				if (args[0].matches("\\d+")) {
					loadPokemonByID(args);
				} else {
					loadPokemonByName(args);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(reader != null){
				reader.close();
			}
			if(scanner != null){
				scanner.close();
			}
		}
	}
	
	public static void loadPokemon() throws IOException {
		loadPokemon(DEFAULT_LOAD_POKEMON);
	}

	private static void loadPokemonByID(String[] args) {
		Pokemon new_pokemon = new Pokemon(args);
		Pokedex.addPokemonToList(new_pokemon);
	}

	private static void loadPokemonByName(String[] args) {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(Integer.toString(Pokedex.getPokemonID(args[0])));
		temp.addAll(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));
		String[] args_to_loadPokemonByID = new String[7];
		temp.toArray(args_to_loadPokemonByID);
		loadPokemonByID(args_to_loadPokemonByID);
	}

	/**
	 * Usage : index name
	 * 
	 * @throws IOException
	 */
	public static void loadPokedex(String filePath) throws IOException {
		try {
			reader = new FileReader(filePath);
			scanner = new Scanner(reader);
			int temp_id;
			String temp_name;
			while (scanner.hasNextLine()) {
				if (scanner.hasNextInt()) {
					temp_id = scanner.nextInt();
					if (scanner.hasNext()) {
						temp_name = scanner.next();
						utility.Pokedex.addPokemonToPokedex(temp_id, temp_name);
					}
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			if (reader != null) {
				reader.close();
			}
		}
	}

	public static void loadPokedex() throws IOException {
		loadPokedex(DEFAULT_LOAD_POKEDEX);
	}

	// Load Fight Map

	public static FightTerrain.TerrainType[][] loadFightMap(String filePath) throws IOException {
		ArrayList<FightTerrain.TerrainType[]> temp_map = new ArrayList<FightTerrain.TerrainType[]>();
		try {
			reader = new FileReader(filePath);
			scanner = new Scanner(reader);
			int widthInBlocks = scanner.nextInt();
			int heightInBlocks = scanner.nextInt();
			for (int line = 0; line < heightInBlocks; line++) {
				temp_map.add(loadFightMapLine(widthInBlocks));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (scanner != null) {
				scanner.close();
			}
		}

		if (temp_map.isEmpty()) {
			return null;
		} else {
			return toFightTerrain2DArray(temp_map);
		}

	}

	public static FightTerrain.TerrainType[][] loadFightMap() throws IOException {
		return loadFightMap(DEFAULT_LOAD_FIGHT_MAP);
	}

	// Load FIght Map Private Methods

	private static FightTerrain.TerrainType[] loadFightMapLine(int width) {
		ArrayList<FightTerrain.TerrainType> temp_map_line = new ArrayList<FightTerrain.TerrainType>();
		for (int i = 0; i < width; i++) {
			temp_map_line.add(FightTerrain.toFightTerrainType(scanner.next()));
		}
		return toFightTerrainArray(temp_map_line);
	}

	private static FightTerrain.TerrainType[] toFightTerrainArray(List<FightTerrain.TerrainType> fightTerrains) {
		TerrainType[] out = new FightTerrain.TerrainType[fightTerrains.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = fightTerrains.get(i);
		}
		return out;
	}

	private static FightTerrain.TerrainType[][] toFightTerrain2DArray(
			List<FightTerrain.TerrainType[]> fightTerrains2D) {
		TerrainType[][] out = new FightTerrain.TerrainType[fightTerrains2D.size()][fightTerrains2D.get(0).length];
		for (int i = 0; i < out.length; i++) {
			out[i] = fightTerrains2D.get(i);
		}
		return out;
	}

}
