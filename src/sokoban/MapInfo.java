package sokoban;

/**
 * Struktura przechowująca informacje o mapie tj. nazwa i jej struktura.
 * 
 * @author Maciej Budrowski
 *
 */
public class MapInfo {
	private String mapStructure, mapName;
	
	public MapInfo() {
		
	}
	
	public MapInfo(String mapName, String mapStructure) {
		this.mapName = mapName;
		this.mapStructure = mapStructure;
	}
	
	/**
	 * Zwraca nazwę mapy
	 * 
	 * @return Nazwa mapy
	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * Ustawia nazwę mapy
	 * 
	 * @param mapName Nazwa mapy
	 */
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	
	/**
	 * Zwraca strukturę mapy
	 * 
	 * @return Struktura mapy
	 */
	public String getMapStructure() {
		return mapStructure;
	}
	
	/**
	 * Ustawia strukturę mapy
	 * 
	 * @param mapStructure Struktura mapy
	 */
	public void setMapStructure(String mapStructure) {
		this.mapStructure = mapStructure;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			MapInfo oth = (MapInfo) obj;
			if (getMapName().equals(oth.getMapName()) && getMapStructure().equals(oth.getMapStructure())) {
				return true;
			}
		}
		catch (ClassCastException e) {
			return false;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		for (int i = 0; i < getMapName().length(); i++) {
		    hash = hash * 31 + getMapName().charAt(i);
		}
		for (int i = 0; i < getMapStructure().length(); i++) {
			hash = hash * 47 + getMapStructure().charAt(i);
		}
		return hash;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getMapName();
	}
}
