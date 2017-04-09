package sokoban.exceptions;

/**
 * Wyjątek wyrzucany, gdy nie znaleziono pliku mapy.
 * 
 * @author Maciej Budrowski
 *
 */
public class InvalidMapFileException extends Exception {
	private static final long serialVersionUID = -2828889797716034314L;

	public InvalidMapFileException() {
		super("Wczytany plik mapy nie jest poprawnym plikiem mapy Sokoban!");
	}
}
