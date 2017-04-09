package sokoban;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sokoban.util.Highscores;
import sokoban.util.Pair;

/**
 * Klasa reprezentująca okno wyświetlające najlepsze wyniki dla map.
 * 
 * @author Maciej Budrowski
 *
 */
public class HighscoreWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8296146904196734747L;

	public HighscoreWindow() {
		this(null);
	}
	
	public HighscoreWindow(MapInfo mapInfo) {
		MapInfo[] mapInfoArray = Highscores.getMaps().toArray(new MapInfo[0]);
		if (mapInfoArray.length == 0) {
			JOptionPane.showMessageDialog(this, "Brak najlepszych wyników.");
			return;
		}
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		JComboBox<MapInfo> mapsDropdownList = new JComboBox<>(mapInfoArray);
		if (mapInfo != null) {
			mapsDropdownList.setSelectedItem(mapInfo);
		}
		mapsDropdownList.setPreferredSize(new Dimension(300, 25));
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(5, 5, 5, 5);
		panel.add(mapsDropdownList, gc);
		
		JPanel scoresPanel = new JPanel();
		scoresPanel.setLayout(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 1;
		mapsDropdownList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				scoresPanel.removeAll();
				GridBagConstraints gc2 = new GridBagConstraints();
				gc2.anchor = GridBagConstraints.NORTHWEST;
				gc2.insets = new Insets(5, 5, 5, 5);
				List<Pair<String, Double>> highscores = Highscores.getHighscoresForMap((MapInfo) mapsDropdownList.getSelectedItem());
				NumberFormat format = new DecimalFormat("0.0");
				int y = 0;
				if (highscores == null) {
					JLabel infoLabel = new JLabel("Brak najlepszych wyników.");
					gc2.gridx = 0;
					gc2.gridy = y++;
					gc2.gridwidth = 2;
					scoresPanel.add(infoLabel, gc2);
				}
				else {
					JLabel infoLabel = new JLabel("Najlepsze wyniki dla tej mapy:");
					gc2.gridx = 0;
					gc2.gridy = y++;
					scoresPanel.add(infoLabel, gc2);
					
					JLabel playerLabel, scoreLabel;
					for (int i = 0; i < highscores.size(); i++) {
						playerLabel = new JLabel((i + 1) + ". " + highscores.get(i).getKey());
						gc2.gridx = 0;
						gc2.gridy = y;
						gc2.gridwidth = 1;
						scoresPanel.add(playerLabel, gc2);
						
						scoreLabel = new JLabel(format.format(highscores.get(i).getValue()) + " s");
						gc2.gridx = 1;
						gc2.gridy = y++;
						gc2.gridwidth = 1;
						scoresPanel.add(scoreLabel, gc2);
					}
				}
				JButton resetHighscores = new JButton("Resetuj wyniki");
				gc2.gridx = 0;
				gc2.gridy = y++;
				gc2.gridwidth = 2;
				resetHighscores.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						int result = JOptionPane.showConfirmDialog(HighscoreWindow.this, 
					            "Czy chcesz usunąć wszystkie najlepsze wyniki?\n"
					            + "Ta operacja jest nieodwracalna!",
					            "Potwierdzenie",
					            JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.YES_OPTION) {
							Highscores.resetHighscores();
							HighscoreWindow.this.dispatchEvent(new WindowEvent(HighscoreWindow.this, WindowEvent.WINDOW_CLOSING));
						}
					}
				});
				scoresPanel.add(resetHighscores, gc2);
				pack();
			}
		});
		panel.add(scoresPanel, gc);
		if (mapInfo != null) {
			mapsDropdownList.setSelectedItem(mapInfo);
		}
		else {
			mapsDropdownList.setSelectedIndex(0);
		}
	
		setContentPane(panel);
		setTitle("Najlepsze wyniki");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
	}
}
