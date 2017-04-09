package sokoban;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import sokoban.exceptions.InvalidMapFileException;

/**
 * Klasa reprezentująca zawartość menu głównego.
 * 
 * @author Maciej Budrowski
 *
 */
@SuppressWarnings("serial")
public class MainMenu extends JPanel {
	
	private static MainMenu instance = new MainMenu();

	/**
	 * Metoda zwracająca istniejącą instancję panelu menu głównego.
	 * 
	 * @return Istniejąca instancja panelu menu głównego.
	 */
	public static MainMenu getInstance() {
		return instance;
	}

	JButton newGameButton;

	private MainMenu() {
		setBackground(Color.cyan);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		JLabel label = new JLabel("");
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(100, 200, 100, 200);
		add(label, c);

		newGameButton = new JButton("Nowa gra");
		newGameButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		newGameButton.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Window.getInstance().changeScreen(LevelSelectScreen.getInstance());
			}
		});
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((JButton) e.getSource()).getActionMap().get("enter").actionPerformed(e);
			}
		});
		c.gridy = 1;
		c.insets = new Insets(15, 200, 15, 200);
		add(newGameButton, c);
		
		JButton loadLevelButton = new JButton("Wczytaj poziom");
		loadLevelButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		loadLevelButton.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(".");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Pliki mapy Sokoban (*.map)", "map");
				fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(MainMenu.this);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            try {
		            	Window.getInstance().changeScreen(new Board(file));
		            }
		            catch (InvalidMapFileException ex) {
						JOptionPane.showMessageDialog(null,
							    ex.getMessage(),
							    "Błąd wczytywania mapy",
							    JOptionPane.ERROR_MESSAGE);
		            }
		        } else {
		        }
			}
		});
		loadLevelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((JButton) e.getSource()).getActionMap().get("enter").actionPerformed(e);
			}
		});
		c.gridy = 2;
		add(loadLevelButton, c);

		JButton highscoresButton = new JButton("Najlepsze wyniki");
		highscoresButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		highscoresButton.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new HighscoreWindow();
			}
		});
		highscoresButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((JButton) e.getSource()).getActionMap().get("enter").actionPerformed(e);
			}
		});
		c.gridy = 3;
		c.insets = new Insets(15, 200, 15, 200);
		add(highscoresButton, c);

		JButton exitButton = new JButton("Wyjście");
		exitButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		exitButton.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((JButton) e.getSource()).getActionMap().get("enter").actionPerformed(e);
			}
		});
		c.gridy = 4;
		add(exitButton, c);
		
		newGameButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		newGameButton.getActionMap().put("down", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadLevelButton.requestFocus();
			}
		});
		exitButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		exitButton.getActionMap().put("up", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				highscoresButton.requestFocus();
			}
		});
		loadLevelButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		loadLevelButton.getActionMap().put("down", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				highscoresButton.requestFocus();
			}
		});
		loadLevelButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		loadLevelButton.getActionMap().put("up", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				newGameButton.requestFocus();
			}
		});
		highscoresButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		highscoresButton.getActionMap().put("down", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exitButton.requestFocus();
			}
		});
		highscoresButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		highscoresButton.getActionMap().put("up", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadLevelButton.requestFocus();
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.drawImage(Resources.getLogoImage(), 10, 5, null);
	}
	
	@Override
	public void requestFocus() {
		super.requestFocus();
		newGameButton.requestFocus();
	}

}
