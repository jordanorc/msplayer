package br.ufes.inf.lprm.msplayer.audio;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class WaveformPanelContainer extends JPanel {

	private final MediaPlayer mediaPlayer;

	private static final long serialVersionUID = 1L;
	private ArrayList<SingleWaveformPanel> singleChannelWaveformPanels = new ArrayList<SingleWaveformPanel>();
	private AudioInfo audioInfo = null;

	public WaveformPanelContainer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
		setLayout(new GridLayout(0, 1));

		Dimension d = getPreferredSize();
		d.height = 150;
		setPreferredSize(d);

		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void opening(MediaPlayer mediaPlayer) {
				String mediaUrl = mediaPlayer.mrl();

				try {
					FileInputStream audio = new FileInputStream(AudioExtractor.extract(new File(new URL(mediaUrl).toURI())));
					setAudioToDisplay(AudioSystem.getAudioInputStream(new BufferedInputStream(audio)));
				} catch (UnsupportedAudioFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}

	public ArrayList<SingleWaveformPanel> GetWaveformPanels() {
		return this.singleChannelWaveformPanels;
	}

	public void SetWaveformPanels(ArrayList<SingleWaveformPanel> value) {
		this.singleChannelWaveformPanels = value;
	}

	public void setAudioToDisplay(AudioInputStream audioInputStream) {
		singleChannelWaveformPanels = new ArrayList<SingleWaveformPanel>();
		audioInfo = new AudioInfo(audioInputStream);
		for (int t = 0; t < audioInfo.getNumberOfChannels(); t++) {
			SingleWaveformPanel waveformPanel = new SingleWaveformPanel(audioInfo, t);
			singleChannelWaveformPanels.add(waveformPanel);
			add(createChannelDisplay(waveformPanel, t));
		}
	}

	private JComponent createChannelDisplay(SingleWaveformPanel waveformPanel, int index) {

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(waveformPanel, BorderLayout.CENTER);

		JLabel label = new JLabel("Channel " + ++index);
		panel.add(label, BorderLayout.NORTH);

		return panel;
	}
}