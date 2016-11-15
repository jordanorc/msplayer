package br.ufes.inf.lprm.msplayer.video;

import java.awt.BorderLayout;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import br.ufes.inf.lprm.msplayer.video.utils.FrameExtractor;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class PlayerVideoInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final MediaPlayer mediaPlayer;

	private JLabel videoInformationLabel;

	private JLabel totalFramesLabel;
	private JLabel totalFrames;

	private JLabel totalIntraFramesLabel;
	private JLabel totalIntraFrames;

	private JLabel totalPredictedFramesLabel;
	private JLabel totalPredictedFrames;

	private JLabel totalBiPredictedFramesLabel;
	private JLabel totalBiPredictedFrames;

	public PlayerVideoInfoPanel(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;

		createUI();
	}

	private void createUI() {
		createControls();
		layoutControls();
		registerListeners();
	}

	private void createControls() {
		videoInformationLabel = new JLabel("Vídeo Information");
		videoInformationLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

		totalFramesLabel = new JLabel("Total Frames: ");
		totalFrames = new JLabel("-");

		totalIntraFramesLabel = new JLabel("Intra Frames: ");
		totalIntraFrames = new JLabel("-");

		totalPredictedFramesLabel = new JLabel("Predicted Frames: ");
		totalPredictedFrames = new JLabel("-");

		totalBiPredictedFramesLabel = new JLabel("Bi-dir predicted Frames: ");
		totalBiPredictedFrames = new JLabel("-");

		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void opening(MediaPlayer mediaPlayer) {
				String mediaUrl = mediaPlayer.mrl();
				FrameExtractor extractor = new FrameExtractor(mediaUrl);
				List<String> frames = extractor.getFrames();

				totalFrames.setText(String.valueOf(frames.size()));

				totalIntraFrames.setText(String
						.valueOf(frames.stream().filter(c -> c.contains("I")).collect(Collectors.toList()).size()));

				totalPredictedFrames.setText(String
						.valueOf(frames.stream().filter(c -> c.contains("P")).collect(Collectors.toList()).size()));

				totalBiPredictedFrames.setText(String
						.valueOf(frames.stream().filter(c -> c.contains("B")).collect(Collectors.toList()).size()));

			}
		});
	}

	private void layoutControls() {
		setBorder(new EmptyBorder(8, 8, 8, 8));

		setLayout(new BorderLayout());

		JPanel slidersPanel = new JPanel();
		slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));

		slidersPanel.add(videoInformationLabel);

		slidersPanel.add(totalFramesLabel);
		slidersPanel.add(totalFrames);

		slidersPanel.add(totalIntraFramesLabel);
		slidersPanel.add(totalIntraFrames);

		slidersPanel.add(totalPredictedFramesLabel);
		slidersPanel.add(totalPredictedFrames);

		slidersPanel.add(totalBiPredictedFramesLabel);
		slidersPanel.add(totalBiPredictedFrames);

		add(slidersPanel, BorderLayout.CENTER);
	}

	private void registerListeners() {

	}
}
