package sokoban;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Klasa odpowiadająca za wczytywanie zasobów aplikacji tj. obrazy.
 * 
 * @author Maciej Budrowski
 *
 */
public class Resources {
	public static final int TILESIZE = 64;
	
	private static Image imagePlayer, imageWall, imageBox, imageWinArea, imageFloor, imageLogo;
	static {
        try {
			imageBox = ImageIO.read(Resources.class.getResource("/resources/box32.png"));
	        imagePlayer = ImageIO.read(Resources.class.getResource("/resources/player32.png"));
	        imageFloor = ImageIO.read(Resources.class.getResource("/resources/floor32.png"));
	        imageWall = ImageIO.read(Resources.class.getResource("/resources/wall32.png"));
	        imageWinArea = ImageIO.read(Resources.class.getResource("/resources/winArea32.png"));
	        imageLogo = ImageIO.read(Resources.class.getResource("/resources/logo.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda zwracająca wybrany obrazek.
	 * 
	 * @return Obrazek gracza.
	 */
	public static Image getPlayerImage() {
		return imagePlayer;
	}

	/**
	 * Metoda zwracająca wybrany obrazek.
	 * 
	 * @return Obrazek ściany.
	 */
	public static Image getWallImage() {
		return imageWall;
	}

	/**
	 * Metoda zwracająca wybrany obrazek.
	 * 
	 * @return Obrazek skrzynki.
	 */
	public static Image getBoxImage() {
		return imageBox;
	}

	/**
	 * Metoda zwracająca wybrany obrazek.
	 * 
	 * @return Obrazek pola wygranej.
	 */
	public static Image getWinAreaImage() {
		return imageWinArea;
	}

	/**
	 * Metoda zwracająca wybrany obrazek.
	 * 
	 * @return Obrazek podłogi.
	 */
	public static Image getFloorImage() {
		return imageFloor;
	}

	/**
	 * Metoda zwracająca wybrany obrazek.
	 * 
	 * @return Obrazek logo aplikacji.
	 */
	public static Image getLogoImage() {
		return imageLogo;
	}
}
