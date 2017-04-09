package sokoban.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import sokoban.MapInfo;

/**
 * Klasa odpowiadająca za zarządzanie najlepszymi wynikami.
 * 
 * @author Maciej Budrowski
 *
 */
public class Highscores {
	
	private static final String FILENAME = "highscores.dat";

	private static Map< MapInfo, List< Pair<String, Double> > > highscores = new HashMap< MapInfo, List< Pair<String,Double> > >();
	private static String lastUsedName;
	
	/**
	 * Metoda zwracająca mapy z najlepszymi wynikami.
	 * 
	 * @return Kolekcję informacji o mapach, które mają przypisane jakieś najlepsze wyniki.
	 */
	public static Collection<MapInfo> getMaps() {
		List<MapInfo> maps = new ArrayList<>(highscores.keySet());
		maps.sort((a, b) -> {
			return a.getMapName().compareTo(b.getMapName());
		});
		return maps;
	}
	
	/**
	 * Metoda zwracająca listę najlepszych wyników dla podanej mapy.
	 * 
	 * @param map Żądana mapa
	 * @return Lista najlepszych wyników dla podanej mapy
	 */
	public static List< Pair<String, Double> > getHighscoresForMap(MapInfo map) {	
		return highscores.get(map);
	}
	
	/**
	 * Metoda zwracająca najlepszy wynik dla podanej mapy.
	 * 
	 * @param mapInfo Żądana mapa
	 * @return Najlepszy czas dla danej mapy.
	 */
	public static Double getHighestScore(MapInfo mapInfo) {
		if (!highscores.containsKey(mapInfo) || highscores.get(mapInfo).size() == 0) {
			return null;
		}
		return ((LinkedList< Pair<String, Double> >) highscores.get(mapInfo)).getLast().getValue();
	}
	
	/**
	 * Metoda zwracająca ostatnie imię użyte do dodawania wyniku.
	 * 
	 * @return Ostatnie użyte imię.
	 */
	public static String getLastUsedName() {
		return lastUsedName;
	}
	
	/**
	 * Metoda dodająca najlepszy wynik do kolekcji. Najlepsze wyniki następnie są zapisywane do pliku.
	 * 
	 * @param mapInfo Obiekt przechowujący informacje o mapie
	 * @param player Imię gracza
	 * @param time Osiągnięty czas
	 */
	public static void addHighscore(MapInfo mapInfo, String player, Double time) {
		if (highscores.containsKey(mapInfo)) {
			List< Pair<String, Double> > list = highscores.get(mapInfo);
			int i;
			for (i = 0; i < list.size(); i++) {
				if (list.get(i).getValue() <= time) {
					continue;
				}
				break;
			}
			list.add(i, new Pair<String, Double>(player, time));
			while (list.size() > 10) {
				list.remove(10);
			}
		}
		else {
			List< Pair<String, Double> > list = new LinkedList<>();
			list.add(new Pair<String, Double>(player, time));
			highscores.put(mapInfo, list);
		}
		lastUsedName = player;
		try {
			saveHighscores();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda wczytująca najlepsze wyniki z pliku.
	 * 
	 * @throws FileNotFoundException Jeśli nie znaleziono pliku najlepszych wyników
	 * @throws JSONException Jeśli struktura pliku najlepszych wyników jest nieprawidłowa
	 */
	public static void loadHighscores() throws FileNotFoundException, JSONException {
		JSONTokener tokener = new JSONTokener(new FileInputStream(new File(FILENAME)));
		JSONObject root = new JSONObject(tokener);
		try {
			lastUsedName = root.getString("lastUsedName");
		}
		catch (JSONException e) {
			lastUsedName = "";
		}
		JSONArray scores = root.getJSONArray("highscores");
		int l = scores.length(), k;
		JSONObject obj, score;
		JSONArray arr;
		for (int i = 0; i < l; i++) {
			obj = scores.getJSONObject(i);
			
			MapInfo mapInfo = new MapInfo(obj.getString("mapName"), obj.getString("mapStructure"));
			List< Pair<String, Double> > list = new LinkedList<>();
			arr = obj.getJSONArray("scores");
			k = Math.min(arr.length(), 10);
			for (int j = 0; j < k; j++) {
				score = arr.getJSONObject(j);
				
				list.add(new Pair<String, Double>(score.getString("player"), score.getDouble("time")));
			}
			
			highscores.put(mapInfo, list);
		}
	}
	
	/**
	 * Metoda zapisująca wszystkie najlepsze wyniki z pamięci do pliku.
	 * 
	 * @throws IOException Jeśli nastąpił błąd wejścia/wyjścia.
	 */
	public static void saveHighscores() throws IOException {
		JSONObject root = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject obj, innerObj;
		JSONArray tmp;
		for (Map.Entry<MapInfo, List< Pair<String, Double> >> i : highscores.entrySet()) {
			obj = new JSONObject();
			
			obj.put("mapName", i.getKey().getMapName());
			obj.put("mapStructure", i.getKey().getMapStructure());
			
			tmp = new JSONArray();
			
			for (Pair<String, Double> j : i.getValue()) {
				innerObj = new JSONObject();
				innerObj.put("player", j.getKey());
				innerObj.put("time", j.getValue());
				
				tmp.put(innerObj);
			}
			
			obj.put("scores", tmp);
			
			arr.put(obj);
		}
		
		root.put("highscores", arr);
		root.put("lastNameUsed", lastUsedName);
		
		FileWriter writer = new FileWriter(new File(FILENAME));
		writer.write(root.toString(4));
		writer.close();
	}
	
	/**
	 * Metoda, która usuwa najlepsze wyniki, a następnie nadpisuje wyniki z pliku.
	 */
	public static void resetHighscores() {
		highscores.clear();
		try {
			saveHighscores();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
