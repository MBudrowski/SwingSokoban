package sokoban;

import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.json.JSONException;

import sokoban.util.Highscores;

/**
 * Klasa reprezentująca główne okno aplikacji. Klasa jest singleton'em, także jedyny dostęp do istniejącego obiektu okna można uzyskać poprzez metodę getInstance().
 * 
 * @author Maciej Budrowski
 *
 */
@SuppressWarnings("serial")
public class Window extends JFrame {
	
	private static Window instance = new Window();
	
	/**
	 * Metoda zwracająca istniejącą instancję okna.
	 * 
	 * @return Istniejąca instancja okna.
	 */
	public static Window getInstance() {
		return instance;
	}
	
	/**
	 * Domyślny konstruktor okna.
	 */
	private Window() {
		super();
	}
	
	/**
	 * Metoda pozwalająca na zmianę zawartości okna, automatyczne wycentrowanie okna oraz nadanie focus'u dla nowo dodanego panelu.
	 * 
	 * @param panel Obiekt klasy JPanel reprezentujący zawartość okna.
	 */
	public void changeScreen(JPanel panel) {
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
		setFocusable(true);
		panel.requestFocus();
	}

	/**
	 * Główna metoda rozpoczynąca działanie programu. Wczytuje najlepsze wyniki z pliku, a następnie tworzy okno aplikacji.
	 * 
	 * @param args Argumenty przekazywane przy uruchamianiu programu (niewykorzystywane).
	 */
	public static void main(String[] args) {
		try {
			Highscores.loadHighscores();
		} catch (FileNotFoundException|JSONException e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = Window.getInstance();
				frame.setTitle("Sokoban");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.setContentPane(MainMenu.getInstance());
				frame.pack();
				frame.setVisible(true);
				frame.setLocationRelativeTo(null);
			}
		});
	}
}
