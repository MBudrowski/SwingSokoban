package sokoban.util;


/**
 * Klasa reprezentująca jeden z czterech głównych kierunków (góra, dół, lewo, prawo).
 * 
 * @author Maciej Budrowski
 *
 */
public class Direction {
	protected int offsetX, offsetY;
	
	protected Direction(int x, int y) {
		offsetX = x;
		offsetY = y;
	}
	
	/**
	 * Metoda zwracająca przesunięcie w osi X.
	 * 
	 * @return Przesunięcie jakie powinno nastąpić w osi X, aby przesunąć się w danym kierunku.
	 */
	public int getOffsetX() {
		return offsetX;
	}
	
	/**
	 * Metoda zwracająca przesunięcie w osi Y.
	 * 
	 * @return Przesunięcie jakie powinno nastąpić w osi Y, aby przesunąć się w danym kierunku.
	 */
	public int getOffsetY() {
		return offsetY;
	}
	
	protected static Direction up = new Direction(0, -1);
	protected static Direction down = new Direction(0, 1);
	protected static Direction left = new Direction(-1, 0);
	protected static Direction right = new Direction(1, 0);
	
	/**
	 * Metoda zwracająca kierunek.
	 * 
	 * @return Obiekt kierunku - góra.
	 */
	public static Direction getUp() {
		return up;
	}
	
	/**
	 * Metoda zwracająca kierunek.
	 * 
	 * @return Obiekt kierunku - dół.
	 */
	public static Direction getDown() {
		return down;
	}

	/**
	 * Metoda zwracająca kierunek.
	 * 
	 * @return Obiekt kierunku - lewo.
	 */
	public static Direction getLeft() {
		return left;
	}

	/**
	 * Metoda zwracająca kierunek.
	 * 
	 * @return Obiekt kierunku - prawo.
	 */
	public static Direction getRight() {
		return right;
	}
	
	protected static Direction[] directions;
	static {
		directions = new Direction[4];
		directions[0] = up;
		directions[1] = right;
		directions[2] = down;
		directions[3] = left;
	}
	
	/**
	 * Metoda zwracająca wszystkie kierunki.
	 * 
	 * @return Wszystkie obiekty kierunków.
	 */
	public static Direction[] getDirections() {
		return directions;
	}
}
