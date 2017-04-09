package sokoban.util;


/**
 * Klasa pomocnicza reprezentująca obiekty klucz-wartość.
 * 
 * @author Maciej Budrowski
 *
 * @param <K> Klasa klucza
 * @param <V> Klasa wartości
 */
public class Pair<K, V> {
	private K key;
	private V value;
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	
	/**
	 * Metoda zwracająca klucz obiektu.
	 * 
	 * @return Klucz obiektu.
	 */
	public K getKey() {
		return key;
	}
	
	/**
	 * Metoda zwracająca wartość obiektu.
	 * 
	 * @return Wartość obiektu.
	 */
	public V getValue() {
		return value;
	}
}
