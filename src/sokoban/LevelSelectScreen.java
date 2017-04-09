package sokoban;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import sokoban.exceptions.InvalidMapFileException;

/**
 * Klasa reprezentująca zawartość ekranu wyboru poziomu.
 * 
 * @author Maciej Budrowski
 *
 */
@SuppressWarnings("serial")
public class LevelSelectScreen extends JPanel {
	
	private static LevelSelectScreen instance = new LevelSelectScreen();

	/**
	 * Metoda zwracająca istniejącą instancję panelu wyboru poziomu.
	 * 
	 * @return Istniejąca instancja panelu wyboru poziomy.
	 */
	public static LevelSelectScreen getInstance() {
		return instance;
	}

	/**
	 * Klasa przypisywana przyciskom w menu wyboru poziomu. Ma na celu zmianę mapy w momencie naciśnięcia klawisza ENTER lub kliknięcia przycisku myszką.
	 * 
	 * @author Maciej Budrowski
	 *
	 */
	private static class LevelChangeAction extends AbstractAction {

		private int id;

		public LevelChangeAction(int id) {
			this.id = id;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Window.getInstance().changeScreen(new Board("/maps/" + id + ".map"));
			} catch (InvalidMapFileException ex) {
				JOptionPane.showMessageDialog(null,
					    ex.getMessage(),
					    "Błąd wczytywania mapy",
					    JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	/**
	 * Klasa przypisywana przyciskom w menu wyboru poziomu. Ma na celu zmianę aktualnie wybranego klawisza przy nawigacji klawiszami strzałek.
	 * 
	 * @author Maciej Budrowski
	 *
	 */
	private static class KeyboardNavigationAction extends AbstractAction {

		private JButton targetButton;

		public KeyboardNavigationAction(JButton targetButton) {
			this.targetButton = targetButton;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			targetButton.requestFocus();
		}

	}

	JButton[] buttons;

	private LevelSelectScreen() {
		setBackground(Color.cyan);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		buttons = new JButton[10];
		for (int i = 1; i <= 10; i++) {
			JButton button = new JButton("Poziom " + i);
			// button.addActionListener(new LevelChanger(i));
			button.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
			button.getActionMap().put("enter", new LevelChangeAction(i));
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					button.getActionMap().get("enter").actionPerformed(e);
				}
			});
			c.gridy = 1 + (i - 1) / 2;
			c.gridx = (i % 2 == 1) ? 0 : 1;
			c.insets = new Insets(10, 10, 10, 10);
			add(button, c);
			buttons[i - 1] = button;
		}

		JButton returnButton = new JButton("Powrót do menu głównego");
		returnButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		returnButton.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Window.getInstance().changeScreen(MainMenu.getInstance());
			}
		});
		returnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((JButton) e.getSource()).getActionMap().get("enter").actionPerformed(e);
			}
		});
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 2;
		add(returnButton, c);

		for (int i = 0; i < 10; i++) {
			buttons[i].getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
			buttons[i].getActionMap().put("left", new KeyboardNavigationAction(buttons[2 * (i / 2) + ((i + 1) % 2)]));
			buttons[i].getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
			buttons[i].getActionMap().put("right", new KeyboardNavigationAction(buttons[2 * (i / 2) + ((i + 1) % 2)]));
			if (i > 1) {
				buttons[i].getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
				buttons[i].getActionMap().put("up", new KeyboardNavigationAction(buttons[i - 2]));
			}
			if (i < 8) {
				buttons[i].getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
						"down");
				buttons[i].getActionMap().put("down", new KeyboardNavigationAction(buttons[i + 2]));
			} else {
				buttons[i].getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
						"down");
				buttons[i].getActionMap().put("down", new KeyboardNavigationAction(returnButton));
			}
		}

		returnButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		returnButton.getActionMap().put("up", new KeyboardNavigationAction(buttons[8]));

		JLabel label = new JLabel("Wybierz poziom:");
		label.setHorizontalAlignment(JLabel.CENTER);
		Font font = label.getFont();
		label.setFont(new Font(font.getName(), Font.BOLD, 24));
		c.gridy = 0;
		add(label, c);

		setFocusable(true);
	}

	@Override
	public void requestFocus() {
		super.requestFocus();
		buttons[0].requestFocus();
	}
}
