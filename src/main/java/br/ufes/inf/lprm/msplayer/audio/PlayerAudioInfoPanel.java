package br.ufes.inf.lprm.msplayer.audio;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class PlayerAudioInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final MediaPlayer mediaPlayer;

	private final JTable tblSilence = new JTable();
	private JLabel audioInformationLabel;

	public PlayerAudioInfoPanel(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;

		createUI();
	}

	private void createUI() {
		createControls();
		layoutControls();
		registerListeners();
	}

	private void createControls() {
		audioInformationLabel = new JLabel("Áudio Information");
		audioInformationLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

		tblSilence.setShowVerticalLines(false);
		tblSilence.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Start", "End", "Duration" }) {
			boolean[] columnEditables = new boolean[] { false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tblSilence.getColumnModel().getColumn(0).setResizable(false);
		tblSilence.getColumnModel().getColumn(1).setResizable(false);
		tblSilence.getColumnModel().getColumn(2).setResizable(false);
		tblSilence.getColumnModel().getColumn(0).setMaxWidth(66);
		tblSilence.getColumnModel().getColumn(1).setMaxWidth(66);
		tblSilence.getColumnModel().getColumn(2).setMaxWidth(67);
		tblSilence.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evnt) {
				if (evnt.getClickCount() == 1) {
					// Menu.container.GetWaveformPanels().get(0).drawTheBeach(silence.get(tblSilence.getSelectedRow()));
				}
			}
		});

		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void opening(MediaPlayer mediaPlayer) {
				String mediaUrl = mediaPlayer.mrl();

				try {
					FileInputStream audio = new FileInputStream(
							AudioExtractor.extract(new File(new URL(mediaUrl).toURI())));
					AudioInfo info = new AudioInfo(AudioSystem.getAudioInputStream(new BufferedInputStream(audio)));
					ArrayList<SilenceInfo> silenceInfo = info.findSilence(0.5, 60);
					populateTable(silenceInfo);
				} catch (UnsupportedAudioFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}

	private void populateTable(ArrayList<SilenceInfo> silence) {
		DefaultTableModel tableSilence = (DefaultTableModel) tblSilence.getModel();

		while (tableSilence.getRowCount() > 0) {
			tableSilence.removeRow(0);
		}
		for (SilenceInfo s : silence) {
			tableSilence.addRow(new Object[] { s.GetStart(), s.GetEnd(), s.GetDuration() });
		}
	}

	private void layoutControls() {
		// setBorder(new LineBorder(Color.RED));
		// add(audioInformationLabel);
		setLayout(new GridLayout(1, 1));
		setBorder(new EmptyBorder(4, 4, 4, 4));
		JScrollPane scroll = new JScrollPane(tblSilence);

		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tblSilence.setPreferredScrollableViewportSize(new Dimension(200, 300));
		tblSilence.setFillsViewportHeight(true);

		add(scroll);

	}

	private void registerListeners() {

	}
}
