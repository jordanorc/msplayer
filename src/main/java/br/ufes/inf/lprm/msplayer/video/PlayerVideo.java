package br.ufes.inf.lprm.msplayer.video;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.WindowUtils;

import br.ufes.inf.lprm.msplayer.audio.PlayerAudioInfoPanel;
import br.ufes.inf.lprm.msplayer.audio.WaveformPanelContainer;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.LibVlcFactory;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.AudioOutput;
import uk.co.caprica.vlcj.player.MediaDetails;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;

public class PlayerVideo extends JPanel {

	/**
	 * Log.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PlayerVideo.class);

	private final Canvas videoSurface;
	private final JPanel controlsPanel;
	private final JPanel videoAdjustPanel;

	private MediaPlayerFactory mediaPlayerFactory;

	private EmbeddedMediaPlayer mediaPlayer;

	public PlayerVideo(JFrame parent) {
		new NativeDiscovery().discover();
		LibVlc libVlc = LibVlcFactory.factory().create();

		videoSurface = new Canvas();

		videoSurface.setBackground(Color.black);
		videoSurface.setSize(800, 200);

		JFrame topFrame = (JFrame) SwingUtilities.windowForComponent(this);
		FullScreenStrategy fullScreenStrategy = new DefaultFullScreenStrategy(parent);

		mediaPlayerFactory = new MediaPlayerFactory();
		mediaPlayerFactory.setUserAgent("vlcj test player");

		List<AudioOutput> audioOutputs = mediaPlayerFactory.getAudioOutputs();
		logger.debug("audioOutputs={}", audioOutputs);

		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer(fullScreenStrategy);
		mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurface));
		mediaPlayer.setPlaySubItems(true);

		mediaPlayer.setEnableKeyInputHandling(false);
		mediaPlayer.setEnableMouseInputHandling(false);

		controlsPanel = new PlayerVideoControlsPanel(mediaPlayer);
		videoAdjustPanel = new JPanel();
		videoAdjustPanel.setLayout(new BoxLayout(videoAdjustPanel, BoxLayout.Y_AXIS));
		// videoAdjustPanel.setLayout(new BoxLayout(videoAdjustPanel,
		// BoxLayout.Y_AXIS));
		videoAdjustPanel.add(new PlayerVideoAdjustPanel(mediaPlayer));
		videoAdjustPanel.add(new PlayerAudioInfoPanel(mediaPlayer));
		videoAdjustPanel.add(new PlayerVideoInfoPanel(mediaPlayer));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(videoSurface);
		mainPanel.add(new PlayerVideoFramePanel(mediaPlayer));
		mainPanel.add(new WaveformPanelContainer(mediaPlayer));

		// mainPanel.setLayout(new MigLayout());
		// mainPanel.add(videoSurface, "width max(100%, 100%), wrap 3");
		// mainPanel.add(new PlayerVideoFramePanel(mediaPlayer), "width
		// max(100%, 100%), wrap");
		// mainPanel.add(new WaveformPanelContainer(mediaPlayer), "width
		// max(100%, 100%), wrap");

		this.setLayout(new BorderLayout());
		this.setBackground(Color.black);
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(controlsPanel, BorderLayout.SOUTH);
		this.add(videoAdjustPanel, BorderLayout.EAST);
		this.invalidate();

		parent.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				logger.debug("windowClosing(evt={})", evt);

				if (mediaPlayer != null) {
					mediaPlayer.release();
					mediaPlayer = null;
				}

				if (mediaPlayerFactory != null) {
					mediaPlayerFactory.release();
					mediaPlayerFactory = null;
				}
			}
		});

		mediaPlayer.addMediaPlayerEventListener(new VideoPlayerMediaPlayerEventListener());

		// Won't work with OpenJDK or JDK1.7, requires a Sun/Oracle JVM
		// (currently)
		boolean transparentWindowsSupport = true;
		try {
			Class.forName("com.sun.awt.AWTUtilities");
		} catch (Exception e) {
			transparentWindowsSupport = false;
		}

		logger.debug("transparentWindowsSupport={}", transparentWindowsSupport);

		if (transparentWindowsSupport) {
			final Window test = new Window(null, WindowUtils.getAlphaCompatibleGraphicsConfiguration()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void paint(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

					g.setColor(Color.white);
					g.fillRoundRect(100, 150, 100, 100, 32, 32);

					g.setFont(new Font("Sans", Font.BOLD, 32));
					g.drawString("Heavyweight overlay test", 100, 300);
				}
			};

			test.setBackground(new Color(0, 0, 0, 0));
		}
	}

	private final class VideoPlayerMediaPlayerEventListener extends MediaPlayerEventAdapter {
		@Override
		public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
			logger.debug("mediaChanged(mediaPlayer={},media={},mrl={})", mediaPlayer, media, mrl);
		}

		@Override
		public void finished(MediaPlayer mediaPlayer) {
			logger.debug("finished(mediaPlayer={})", mediaPlayer);
		}

		@Override
		public void paused(MediaPlayer mediaPlayer) {
			logger.debug("paused(mediaPlayer={})", mediaPlayer);
		}

		@Override
		public void playing(MediaPlayer mediaPlayer) {
			logger.debug("playing(mediaPlayer={})", mediaPlayer);
			MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
			logger.info("mediaDetails={}", mediaDetails);
		}

		@Override
		public void stopped(MediaPlayer mediaPlayer) {
			logger.debug("stopped(mediaPlayer={})", mediaPlayer);
		}

		@Override
		public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
			logger.debug("videoOutput(mediaPlayer={},newCount={})", mediaPlayer, newCount);
			if (newCount == 0) {
				return;
			}

			MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
			logger.info("mediaDetails={}", mediaDetails);

			MediaMeta mediaMeta = mediaPlayer.getMediaMeta();
			logger.info("mediaMeta={}", mediaMeta);

			final Dimension dimension = mediaPlayer.getVideoDimension();
			logger.debug("dimension={}", dimension);
			if (dimension != null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						// videoSurface.setSize(dimension);
						// TODO: improve here
						// VideoPlayer.this.pack();
					}
				});
			}

			// You can set a logo like this if you like...
			File logoFile = new File("./etc/vlcj-logo.png");
			if (logoFile.exists()) {
				mediaPlayer.setLogoFile(logoFile.getAbsolutePath());
				mediaPlayer.setLogoOpacity(0.5f);
				mediaPlayer.setLogoLocation(10, 10);
				mediaPlayer.enableLogo(true);
			}
		}

	}

}
