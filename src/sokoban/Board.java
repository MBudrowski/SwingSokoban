package sokoban;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import sokoban.exceptions.InvalidMapFileException;
import sokoban.util.Direction;
import sokoban.util.Highscores;
import sokoban.util.Pair;

/**
 * Klasa reprezentująca widok gry na ekranie. Widok zawiera pasek z informacjami na górze ekranu oraz planszę właściwą poniżej.
 * 
 * @author Maciej Budrowski
 *
 */
@SuppressWarnings("serial")
public class Board extends JPanel {

	public static final int PANEL_WIDTH = 300;
	public static final int HEADER_HEIGHT = 100;
	public static final int MIN_BOARD_HEIGHT = 300;

	/**
	 * Klasa nasłuchująca na klawisze klawiatury.
	 * 
	 * @author Maciej Budrowski
	 *
	 */
	protected class KeyboardListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			switch (key) {
			case KeyEvent.VK_ENTER:
				Board.this.startTimer();
				break;
			case KeyEvent.VK_ESCAPE:
				Board.this.returnToMainMenu();
				break;
			case KeyEvent.VK_R:
				restartMap();
				break;
			}
			if (!acceptInput || playerInstance == null) {
				return;
			}

			switch (key) {
			case KeyEvent.VK_UP:
				playerInstance.move(Direction.getUp());
				break;
			case KeyEvent.VK_DOWN:
				playerInstance.move(Direction.getDown());
				break;
			case KeyEvent.VK_LEFT:
				playerInstance.move(Direction.getLeft());
				break;
			case KeyEvent.VK_RIGHT:
				playerInstance.move(Direction.getRight());
				break;
			default:
				return;
			}
			repaint();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
		}

	}

	/**
	 * Klasa panelu widoku informacji o mapie.
	 * 
	 * @author Maciej Budrowski
	 *
	 */
	protected class InfoPanel extends JPanel {
		public InfoPanel() {
			setBackground(new Color(0, 220, 255));
			setLayout(new GridBagLayout());

			GridBagConstraints c2 = new GridBagConstraints();
			mapNameLabel = new JLabel();
			mapNameLabel.setPreferredSize(new Dimension(300, 20));
			c2.anchor = GridBagConstraints.NORTHWEST;
			c2.gridx = 0;
			c2.gridy = 0;
			c2.insets = new Insets(5, 5, 0, 0);
			add(mapNameLabel, c2);
			timeLeftLabel = new JLabel();
			timeLeftLabel.setPreferredSize(new Dimension(300, 20));
			c2.anchor = GridBagConstraints.SOUTHWEST;
			c2.gridx = 0;
			c2.gridy = 1;
			c2.insets = new Insets(0, 5, 5, 0);
			add(timeLeftLabel, c2);

			JButton restartButton = new JButton("Restart poziomu");
			c2.anchor = GridBagConstraints.CENTER;
			c2.gridx = 1;
			c2.gridy = 0;
			c2.insets = new Insets(5, 5, 5, 5);
			c2.gridheight = 2;
			restartButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					restartMap();
					Board.this.requestFocus();
				}
			});
			add(restartButton, c2);

			JButton backButton = new JButton("Wróć do menu");
			c2.anchor = GridBagConstraints.CENTER;
			c2.gridx = 2;
			c2.gridy = 0;
			c2.insets = new Insets(5, 5, 5, 5);
			c2.gridheight = 2;
			backButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					returnToMainMenu();
				}
			});
			add(backButton, c2);

			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		}
	}

	/**
	 * Klasa panelu widoku planszy gry.
	 * 
	 * @author Maciej Budrowski
	 *
	 */
	protected class MainPanel extends JPanel {

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(Math.max(Resources.TILESIZE * boardWidth, infoPanel.getPreferredSize().width),
					Math.max(Resources.TILESIZE * boardHeight, MIN_BOARD_HEIGHT));
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (mapStarted) {
				Graphics2D g2d = (Graphics2D) g;
				AffineTransform oldTr = g2d.getTransform();
				AffineTransform tr = new AffineTransform();
				tr.translate((getPreferredSize().getWidth() - Resources.TILESIZE * boardWidth) / 2,
						(getPreferredSize().getHeight() - Resources.TILESIZE * boardHeight) / 2);
				tr.scale(5.0, 5.0);
				g2d.transform(tr);
				for (GameObject proto : mapPrototypeInstances.keySet()) {
					for (GameObject go : mapPrototypeInstances.get(proto)) {
						go.draw((Graphics2D) g, mapPrototypeToImage.get(proto));
					}
				}
				playerInstance.draw((Graphics2D) g, mapPrototypeToImage.get(playerInstance));
				g2d.setTransform(oldTr);
			}
		}
	}

	/**
	 * Licznik służący do zmniejszania czasu pozostałego do zakończenia mapy. Jeśli czas się wyczerpie to zostanie ustawiona odpowiednia flaga w obiekcie planszy, a zegar zakończy działanie.
	 * 
	 * @author Maciej Budrowski
	 *
	 */
	protected class CustomTimer extends Timer implements ActionListener {

		public CustomTimer() {
			super(100, null);
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			timeRemaining -= 0.1;
			if (timeRemaining <= 0) {
				timeRemaining = 0.0;
				acceptInput = false;
				mapEnded = true;
				System.out.println("PRZEGRANA!");
				stop();
			}
			timeLeftLabel.setText("Pozostały czas: " + (new DecimalFormat("0.0").format(timeRemaining)) + "s");
			if (timeRemaining <= 0) {
				popupWindow = new JFrame("Wyniki");
				popupWindow.setContentPane(new HighscorePanel(-1.0));
				popupWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				popupWindow.setResizable(false);
				popupWindow.pack();
				popupWindow.setVisible(true);
				popupWindow.setLocationRelativeTo(null);
			}
		}

	}

	/**
	 * Klasa reprezentująca ekran dodawania najlepszych wyników po pomyślnym zakończeniu mapy.
	 * 
	 * @author Maciej Budrowski
	 *
	 */
	protected class HighscorePanel extends JPanel {
		public HighscorePanel(double score) {
			setLayout(new GridBagLayout());

			GridBagConstraints c2 = new GridBagConstraints();
			JLabel label = new JLabel("Wygrana!");
			if (score < 0) {
				label.setText("Przegrana!");
			}
			label.setFont(new Font(label.getFont().getFontName(), label.getFont().getStyle(), 24));
			c2.anchor = GridBagConstraints.NORTHWEST;
			c2.gridx = 0;
			c2.gridy = 0;
			c2.gridwidth = 2;
			c2.insets = new Insets(5, 5, 5, 5);
			add(label, c2);

			MapInfo mapInfo = new MapInfo(mapName, mapStructure);
			LinkedList<Pair<String, Double>> highscores = (LinkedList<Pair<String, Double>>) Highscores
					.getHighscoresForMap(mapInfo);

			NumberFormat format = new DecimalFormat("0.0");
			int y = 1;
			if (highscores == null) {
				JLabel infoLabel = new JLabel("Brak najlepszych wyników.");
				c2.anchor = GridBagConstraints.SOUTHWEST;
				c2.gridx = 0;
				c2.gridy = y++;
				c2.gridwidth = 2;
				add(infoLabel, c2);
			} else {
				JLabel infoLabel = new JLabel("Najlepsze wyniki dla tej mapy:");
				c2.anchor = GridBagConstraints.SOUTHWEST;
				c2.gridx = 0;
				c2.gridy = y++;
				add(infoLabel, c2);

				JLabel playerLabel, scoreLabel;
				for (int i = 0; i < highscores.size(); i++) {
					playerLabel = new JLabel((i + 1) + ". " + highscores.get(i).getKey());
					c2.anchor = GridBagConstraints.SOUTHWEST;
					c2.gridx = 0;
					c2.gridy = y;
					c2.gridwidth = 1;
					add(playerLabel, c2);

					scoreLabel = new JLabel(format.format(highscores.get(i).getValue()) + " s");
					c2.anchor = GridBagConstraints.SOUTHWEST;
					c2.gridx = 1;
					c2.gridy = y++;
					c2.gridwidth = 1;
					add(scoreLabel, c2);
				}
			}

			if (score >= 0) {
				JLabel yourScoreLabel = new JLabel("Twój wynik: ");
				c2.anchor = GridBagConstraints.SOUTHWEST;
				c2.gridx = 0;
				c2.gridy = y;
				c2.gridwidth = 1;
				add(yourScoreLabel, c2);

				JLabel yourScoreNumberLabel = new JLabel(format.format(score) + " s");
				c2.anchor = GridBagConstraints.SOUTHWEST;
				c2.gridx = 1;
				c2.gridy = y++;
				c2.gridwidth = 1;
				add(yourScoreNumberLabel, c2);

				if (highscores == null || highscores.size() < 10 || score < highscores.getLast().getValue()) {
					JLabel resultLabel = new JLabel("Czy chcesz dodać swój wynik do listy wyników?");
					c2.anchor = GridBagConstraints.SOUTHWEST;
					c2.gridx = 0;
					c2.gridy = y++;
					c2.gridwidth = 2;
					add(resultLabel, c2);

					JLabel yourNameLabel = new JLabel("Twoje imię:");
					c2.anchor = GridBagConstraints.SOUTHWEST;
					c2.gridx = 0;
					c2.gridy = y;
					c2.gridwidth = 1;
					add(yourNameLabel, c2);

					JTextField textField = new JTextField("", 16);
					c2.anchor = GridBagConstraints.SOUTHWEST;
					c2.gridx = 1;
					c2.gridy = y++;
					c2.gridwidth = 1;
					textField.setDocument(new PlainDocument() {
						@Override
						public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
							if (str == null)
								return;

							if ((getLength() + str.length()) <= 20) {
								super.insertString(offs, str, a);
							}
						}
					});
					textField.setText(Highscores.getLastUsedName());
					add(textField, c2);

					textField.requestFocus();

					JButton yesButton = new JButton("Tak");
					yesButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (textField.getText() == null || textField.getText().isEmpty()) {
								return;
							}
							MapInfo mapInfo = new MapInfo(mapName, mapStructure);
							Highscores.addHighscore(mapInfo, textField.getText(),
									Math.round((timeLimit - timeRemaining) * 10.0) / 10.0);
							popupWindow.dispatchEvent(new WindowEvent(popupWindow, WindowEvent.WINDOW_CLOSING));
							popupWindow = null;
							new HighscoreWindow(mapInfo);
						}
					});
					c2.anchor = GridBagConstraints.SOUTHWEST;
					c2.gridx = 0;
					c2.gridy = y;
					c2.gridwidth = 1;
					add(yesButton, c2);

					JButton noButton = new JButton("Nie");
					noButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							popupWindow.dispatchEvent(new WindowEvent(popupWindow, WindowEvent.WINDOW_CLOSING));
							popupWindow = null;
						}
					});
					c2.anchor = GridBagConstraints.SOUTHWEST;
					c2.gridx = 1;
					c2.gridy = y++;
					c2.gridwidth = 1;
					add(noButton, c2);
				} else {
					JLabel resultLabel = new JLabel(
							"Niestety, twój wynik nie kwalifikuje się do listy najlepszych wyników. Spróbuj jeszcze raz!");
					c2.anchor = GridBagConstraints.SOUTHWEST;
					c2.gridx = 0;
					c2.gridy = y++;
					c2.gridwidth = 2;
					add(resultLabel, c2);

					JButton leaveButton = new JButton("Wyjdź");
					leaveButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							popupWindow.dispatchEvent(new WindowEvent(popupWindow, WindowEvent.WINDOW_CLOSING));
							popupWindow = null;
						}
					});
					c2.anchor = GridBagConstraints.SOUTHWEST;
					c2.gridx = 0;
					c2.gridy = y++;
					c2.gridwidth = 2;
					add(leaveButton, c2);
				}
			}
			else {
				JLabel yourScoreLabel = new JLabel(
						"Niestety nie udało ci się zakończyć poziomu pomyślnie. Spróbuj ponownie!");
				c2.anchor = GridBagConstraints.SOUTHWEST;
				c2.gridx = 0;
				c2.gridy = y++;
				c2.gridwidth = 2;
				add(yourScoreLabel, c2);

				JButton leaveButton = new JButton("Wyjdź");
				leaveButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						popupWindow.dispatchEvent(new WindowEvent(popupWindow, WindowEvent.WINDOW_CLOSING));
						popupWindow = null;
					}
				});
				c2.anchor = GridBagConstraints.SOUTHWEST;
				c2.gridx = 0;
				c2.gridy = y++;
				c2.gridwidth = 2;
				add(leaveButton, c2);
			}
		}
	}

	protected JPanel infoPanel, centerPanel;
	protected JLabel mapNameLabel, timeLeftLabel, pressEnterLabel;
	protected int boardWidth, boardHeight;
	protected String mapName, fileName;
	protected File file;
	protected GameObject playerInstance, wallProto, boxProto, winAreaProto, floorProto;
	protected Map<GameObject, List<GameObject>> mapPrototypeInstances;
	protected Map<GameObject, Image> mapPrototypeToImage;
	protected double timeRemaining;
	protected int timeLimit;
	protected CustomTimer timer;
	protected boolean mapStarted = false, mapEnded = false, acceptInput = false;
	protected String mapStructure = "";
	protected JFrame popupWindow;

	public Board(String fileName) throws InvalidMapFileException {
		setupLayout();
		createPrototypes();
		initializeBoard(fileName);
		mapPrototypeToImage.put(playerInstance, Resources.getPlayerImage());
		setFocusable(true);
		addKeyListener(new KeyboardListener());
	}

	public Board(File file) throws InvalidMapFileException {
		setupLayout();
		createPrototypes();
		initializeBoard(file);
		mapPrototypeToImage.put(playerInstance, Resources.getPlayerImage());
		setFocusable(true);
		addKeyListener(new KeyboardListener());
	}

	/**
	 * Metoda, która ma za zadanie ustawić układ wszystkich kontrolek i panelów na ekranie.
	 */
	protected void setupLayout() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		infoPanel = new InfoPanel();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		// infoPanel.setPreferredSize(new Dimension(PANEL_WIDTH,
		// HEADER_HEIGHT));
		add(infoPanel, c);

		centerPanel = new MainPanel();
		centerPanel.setBackground(new Color(0, 255, 255));
		pressEnterLabel = new JLabel("Naciśnij ENTER, aby rozpocząć...");
		pressEnterLabel.setFont(new Font("Arial", Font.BOLD, 24));
		pressEnterLabel.setHorizontalAlignment(JLabel.CENTER);
		pressEnterLabel.setVerticalAlignment(JLabel.CENTER);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(pressEnterLabel, BorderLayout.CENTER);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		add(centerPanel, c);
	}

	/**
	 * Metoda inicjalizująca planszę znajdujacą się w archiwum JAR aplikacji.
	 * 
	 * @param fileName Ścieżka do pliku planszy w archiwum JAR aplikacji.
	 * @throws InvalidMapFileException Jeśli plik mapy ma nieprawidłowy format.
	 */
	protected void initializeBoard(String fileName) throws InvalidMapFileException {
		this.fileName = fileName;
		initializeBoard(() -> {
			return getClass().getResourceAsStream(fileName);
		});
	}

	/**
	 * Metoda inicjalizująca planszę znajdujacą się na dysku użytkownika.
	 * 
	 * @param file Ścieżka do pliku planszy na dysku użytkownika.
	 * @throws InvalidMapFileException Jeśli plik mapy ma nieprawidłowy format.
	 */
	protected void initializeBoard(File file) throws InvalidMapFileException {
		this.file = file;
		initializeBoard(() -> {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	/**
	 * Metoda inicjalizująca planszę.
	 * 
	 * @param inputStreamSupplier Dostawca strumienia wejścia dla pliku mapy.
	 * @throws InvalidMapFileException Jeśli plik mapy ma nieprawidłowy format.
	 */
	protected void initializeBoard(Supplier<InputStream> inputStreamSupplier) throws InvalidMapFileException {
		BufferedReader fin = null;
		fin = new BufferedReader(new InputStreamReader(inputStreamSupplier.get()));

		String s = null;
		String[] tmp = null;
		int mapX = -1, mapY = -1;
		int iTmp;

		try {
			while (fin.ready()) {
				s = fin.readLine();
				if (mapX != -1 && mapY != -1) {
					iTmp = s.length();
					if (iTmp > mapX) {
						mapX = iTmp;
					}
					mapY++;
				}
				tmp = s.split("=");
				if (tmp[0].equals("map")) {
					mapX = mapY = 0;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			try {
				fin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		if (mapX == -1 || mapY == -1) {
			throw new InvalidMapFileException();
		}

		boardWidth = mapX;
		boardHeight = mapY;
		mapX = mapY = -1;
		mapStructure = "";

		fin = new BufferedReader(new InputStreamReader(inputStreamSupplier.get()));

		try {
			while (fin.ready()) {
				s = fin.readLine();
				if (s.isEmpty()) {
					continue;
				}
				if (mapX != -1 && mapY != -1) {
					iTmp = s.length();
					System.out.println(s);
					mapStructure = mapStructure.concat(s) + '\n';
					for (int i = 0; i < iTmp; i++) {
						if (s.charAt(i) == '#') {
							placeObject(wallProto, i, mapY);
						} else if (s.charAt(i) == 'p') {
							playerInstance = GameObject.placeInstance(new GameObject(this, false, true), i, mapY);
						} else if (s.charAt(i) == 'b') {
							placeObject(boxProto, i, mapY);
						} else if (s.charAt(i) == 'X') {
							placeObject(winAreaProto, i, mapY);
						} else if (s.charAt(i) == 'B') {
							placeObject(boxProto, i, mapY);
							placeObject(winAreaProto, i, mapY);
						} else if (s.charAt(i) == 'P') {
							placeObject(winAreaProto, i, mapY);
							playerInstance = GameObject.placeInstance(new GameObject(this, false, true), i, mapY);
						}
					}
					mapY++;
				}
				tmp = s.split("=");
				switch (tmp[0]) {
				case "name":
					mapName = new String(tmp[1]);
					mapNameLabel.setText("Nazwa mapy: " + mapName);
					break;
				case "timeLimit":
					timeLimit = Integer.parseInt(tmp[1]);
					timeRemaining = timeLimit;
					timeLeftLabel.setText("Pozostały czas: " + (new DecimalFormat("0.0").format(timeRemaining)) + "s");
					break;
				case "map":
					mapX = mapY = 0;
					break;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		floorfillFloor();

		try {
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Uruchamia zegar odliczający czas mapy.
	 */
	protected void startTimer() {
		if (mapStarted) {
			return;
		}
		pressEnterLabel.setVisible(false);
		if (timer != null) {
			timer.stop();
		}
		timer = new CustomTimer();
		timer.start();
		mapStarted = true;
		acceptInput = true;
		repaint();
	}

	/**
	 * Tworzy prototypy podstawowych obiektów mapy.
	 */
	protected void createPrototypes() {
		mapPrototypeInstances = new LinkedHashMap<>();
		mapPrototypeToImage = new HashMap<>();
		floorProto = new GameObject(this, true, false);
		mapPrototypeInstances.put(floorProto, new ArrayList<>());
		mapPrototypeToImage.put(floorProto, Resources.getFloorImage());
		wallProto = new GameObject(this, false, false);
		mapPrototypeInstances.put(wallProto, new ArrayList<>());
		mapPrototypeToImage.put(wallProto, Resources.getWallImage());
		winAreaProto = new GameObject(this, true, false);
		mapPrototypeInstances.put(winAreaProto, new ArrayList<>());
		mapPrototypeToImage.put(winAreaProto, Resources.getWinAreaImage());
		boxProto = new GameObject(this, false, true);
		mapPrototypeInstances.put(boxProto, new ArrayList<>());
		mapPrototypeToImage.put(boxProto, Resources.getBoxImage());
	}

	/**
	 * Restartuje mapę po naciścięniu przycisku przez użytkownika.
	 */
	protected void restartMap() {
		if (timer != null) {
			timer.stop();
		}
		timer = null;
		mapStarted = false;
		mapEnded = false;
		acceptInput = false;
		createPrototypes();
		try {
			if (file != null) {
				initializeBoard(file);
			} else {
				initializeBoard(fileName);
			}
		} catch (InvalidMapFileException e) {
			JOptionPane.showConfirmDialog(this, e.getMessage(), "Błąd wczytywania mapy", JOptionPane.ERROR_MESSAGE);
			Window.getInstance().changeScreen(MainMenu.getInstance());
		}
		mapPrototypeToImage.put(playerInstance, Resources.getPlayerImage());
		pressEnterLabel.setVisible(true);
		repaint();
	}

	/**
	 * Przechodzi do ekranu menu głównego.
	 */
	protected void returnToMainMenu() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
		Window.getInstance().changeScreen(MainMenu.getInstance());
	}

	/**
	 * Metoda pomocnicza mająca na celu wypełnienie planszy obiektami podłogi korzystając z algorytmy floodfill zaczynając od obiektu gracza.
	 */
	protected void floorfillFloor() {
		if (playerInstance == null) {
			return;
		}
		Queue<Pair<Integer, Integer>> remaining = new LinkedList<>();
		List<Pair<Integer, Integer>> visited = new LinkedList<>();
		remaining.add(playerInstance.getCoordinates());
		Pair<Integer, Integer> current, newPair;
		while (!remaining.isEmpty()) {
			current = remaining.remove();
			if (isOnTheList(visited, current)) {
				continue;
			}
			visited.add(current);
			if (isValidFloodfillTile(current)) {
				try {
					placeObject(floorProto, current.getKey(), current.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (Direction d : Direction.getDirections()) {
					newPair = new Pair<Integer, Integer>(current.getKey() + d.getOffsetX(),
							current.getValue() + d.getOffsetY());
					if (!isOnTheList(visited, newPair)) {
						remaining.add(newPair);
					}
				}
			}
		}
	}

	/**
	 * Metoda pomocnicza, która sprawdza czy dana para koordynatów znajduje się na liście.
	 * 
	 * @param list Lista współrzędnych
	 * @param coord Szukana współrzędna
	 * @return True, jeśli współrzędna jest na liście.
	 */
	protected static boolean isOnTheList(List<Pair<Integer, Integer>> list, Pair<Integer, Integer> coord) {
		for (Pair<Integer, Integer> i : list) {
			if (i.getKey() == coord.getKey() && i.getValue() == coord.getValue()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Metoda pomocnicza w trakcie algorytmu floodfill. Ma na celu sprawdzenie, czy dana współrzędna mapy nadaje się do kontynuacji działania algorytmu (czyli jeśli współrzędne są prawidłowe i jeśli nie ma ściany na danej współrzędnej).
	 * 
	 * @param coord Współrzędna mapy
	 * @return True, jeśli współrzędna jest prawidłowa do kontynuacji wypełniania mapy.
	 */
	protected boolean isValidFloodfillTile(Pair<Integer, Integer> coord) {
		if (coord.getKey() < 0 || coord.getKey() >= boardWidth || coord.getValue() < 0
				|| coord.getValue() >= boardHeight) {
			return false;
		}
		for (GameObject wall : mapPrototypeInstances.get(wallProto)) {
			if (wall.getX() == coord.getKey() && wall.getY() == coord.getValue()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Metoda sprawdzająca czy dany obiekt skrzynki znajduje się na dowolnym polu wygranej.
	 * 
	 * @param obj Obiekt skrzynki
	 * @return True, jeśli skrzynka jest na polu wygranej.
	 */
	protected boolean isBoxOnWinArea(GameObject obj) {
		for (GameObject winArea : mapPrototypeInstances.get(winAreaProto)) {
			if (winArea.getX() == obj.getX() && winArea.getY() == obj.getY()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Metoda sprawdzająca, czy gracz wygrał grę poprzez ustawienie wszystkich skrzynek na pola wygranej. Jeśli tak się stało, to zostaje wyświetlony komunikat o najlepszych wynikach, a mapa zostaje zatrzymana.
	 */
	protected void checkCompletion() {
		boolean hasGameBeenWon = true;
		for (GameObject box : mapPrototypeInstances.get(boxProto)) {
			if (!isBoxOnWinArea(box)) {
				hasGameBeenWon = false;
				break;
			}
		}
		if (hasGameBeenWon) {
			mapEnded = true;
			acceptInput = false;
			System.out.println("WYGRANA!");
			if (timer != null) {
				timer.stop();
			}
			timer = null;
			/*
			 * JOptionPane.showMessageDialog(Board.this, "Wygrałeś!",
			 * "Koniec gry!", JOptionPane.PLAIN_MESSAGE);
			 */
			popupWindow = new JFrame("Wyniki");
			popupWindow.setContentPane(new HighscorePanel(Math.round((timeLimit - timeRemaining) * 10.0) / 10.0));
			popupWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			popupWindow.setResizable(false);
			popupWindow.pack();
			popupWindow.setVisible(true);
			popupWindow.setLocationRelativeTo(null);
		}
	}

	/**
	 * Metoda umieszczająca obiekt o podanym prototypie na danych współrzędnych X i Y mapy.
	 * 
	 * @param obj Prototyp obiektu
	 * @param x Współrzędna X
	 * @param y Współrzędna Y
	 * @return Instancja obiektu umieszczona na mapie.
	 * @throws Exception Jeśli prototyp nie znajduje się w słowniku prototypów planszy.
	 */
	protected GameObject placeObject(GameObject obj, int x, int y) throws Exception {
		if (x < 0 || y < 0 || x >= boardWidth || y >= boardHeight) {
			return null;
		}
		if (!mapPrototypeInstances.containsKey(obj)) {
			throw new Exception("Board::placeObject - Object is not in the dictionary!");
		}
		GameObject go = GameObject.placeInstance(obj, x, y);
		mapPrototypeInstances.get(obj).add(go);
		return go;
	}

	/**
	 * Pobiera najbardziej pierwszoplanowy obiekt (kolejno: gracz, skrzynka, pole wygranej, ściana, podłoga) na danych współrzędnych na mapie.
	 * 
	 * @param x Współrzędna X
	 * @param y Współrzędna Y
	 * @return Instancja obiektu lub null, jeśli taki nie istnieje.
	 */
	public GameObject getObjectAt(int x, int y) {
		if (playerInstance.getX() == x && playerInstance.getY() == y) {
			return playerInstance;
		}
		LinkedList<GameObject> list = new LinkedList<>(mapPrototypeInstances.keySet());
		Iterator<GameObject> it = list.descendingIterator();
		while (it.hasNext()) {
			for (GameObject go : mapPrototypeInstances.get(it.next())) {
				if (go.getX() == x && go.getY() == y) {
					return go;
				}
			}
		}
		return null;
	}

	/**
	 * Zwraca szerokość planszy w pikselach.
	 * 
	 * @return Szerokość planszy w pikselach.
	 */
	public int getBoardWidth() {
		return boardWidth;
	}

	/**
	 * Zwraca wysokość planszy w pikselach.
	 * 
	 * @return Wysokość planszy w pikselach.
	 */
	public int getBoardHeight() {
		return boardHeight;
	}

	/**
	 * Zwracza czy mapa się skończyła.
	 * 
	 * @return True, jeśli mapa się skończyła (wygraną lub przegraną), false w przeciwnym wypadku.
	 */
	public boolean hasEnded() {
		return mapEnded;
	}
}
