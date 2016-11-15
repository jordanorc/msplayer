package br.ufes.inf.lprm.msplayer.video;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.WindowUtils;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.LibVlcFactory;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
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

	public static void main(final String[] args) throws Exception {
		LibVlc libVlc = LibVlcFactory.factory().create();

		logger.info("  version: {}", libVlc.libvlc_get_version());
		logger.info(" compiler: {}", libVlc.libvlc_get_compiler());
		logger.info("changeset: {}", libVlc.libvlc_get_changeset());
	}

	public PlayerVideo(JFrame parent) {
		videoSurface = new Canvas();

		videoSurface.setBackground(Color.black);
		videoSurface.setSize(800, 500); // Only for initial layout

		// Since we're mixing lightweight Swing components and heavyweight AWT
		// components this is probably a good idea
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		VideoPlayerMouseListener mouseListener = new VideoPlayerMouseListener();
		videoSurface.addMouseListener(mouseListener);
		videoSurface.addMouseMotionListener(mouseListener);
		videoSurface.addMouseWheelListener(mouseListener);
		videoSurface.addKeyListener(new VideoPlayerKeyListener());

		List<String> vlcArgs = new ArrayList<String>();

		vlcArgs.add("--no-snapshot-preview");
		vlcArgs.add("--quiet");
		vlcArgs.add("--quiet-synchro");
		vlcArgs.add("--intf");
		vlcArgs.add("dummy");

		// Special case to help out users on Windows (supposedly this is not
		// actually needed)...
		// if(RuntimeUtil.isWindows()) {
		// vlcArgs.add("--plugin-path=" + WindowsRuntimeUtil.getVlcInstallDir()
		// + "\\plugins");
		// }
		// else {
		// vlcArgs.add("--plugin-path=/home/linux/vlc/lib");
		// }

		// vlcArgs.add("--plugin-path=" + System.getProperty("user.home") +
		// "/.vlcj");

		logger.debug("vlcArgs={}", vlcArgs);

		JFrame topFrame = (JFrame) SwingUtilities.windowForComponent(this);
		FullScreenStrategy fullScreenStrategy = new DefaultFullScreenStrategy(parent);

		mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
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
		videoAdjustPanel.add(new PlayerVideoAdjustPanel(mediaPlayer));
		videoAdjustPanel.add(new PlayerVideoInfoPanel(mediaPlayer));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(videoSurface);
		mainPanel.add(new PlayerVideoFramePanel(mediaPlayer));

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

		// Global AWT key handler, you're better off using Swing's InputMap and
		// ActionMap with a JFrame - that would solve all sorts of focus issues
		// too
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				if (event instanceof KeyEvent) {
					KeyEvent keyEvent = (KeyEvent) event;
					if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
						if (keyEvent.getKeyCode() == KeyEvent.VK_F12) {
							controlsPanel.setVisible(!controlsPanel.isVisible());
							videoAdjustPanel.setVisible(!videoAdjustPanel.isVisible());
							// mainFrame.getJMenuBar().setVisible(!mainFrame.getJMenuBar().isVisible());
							// mainFrame.invalidate();
							// mainFrame.validate();
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_A) {
							mediaPlayer.setAudioDelay(mediaPlayer.getAudioDelay() - 50000);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_S) {
							mediaPlayer.setAudioDelay(mediaPlayer.getAudioDelay() + 50000);
						}
						// else if(keyEvent.getKeyCode() == KeyEvent.VK_N) {
						// mediaPlayer.nextFrame();
						// }
						else if (keyEvent.getKeyCode() == KeyEvent.VK_1) {
							mediaPlayer.setTime(60000 * 1);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_2) {
							mediaPlayer.setTime(60000 * 2);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_3) {
							mediaPlayer.setTime(60000 * 3);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_4) {
							mediaPlayer.setTime(60000 * 4);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_5) {
							mediaPlayer.setTime(60000 * 5);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_6) {
							mediaPlayer.setTime(60000 * 6);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_7) {
							mediaPlayer.setTime(60000 * 7);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_8) {
							mediaPlayer.setTime(60000 * 8);
						} else if (keyEvent.getKeyCode() == KeyEvent.VK_9) {
							mediaPlayer.setTime(60000 * 9);
						}
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

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

			// AWTUtilities.setWindowOpaque(test, false); // Doesn't work in
			// full-screen exclusive
			// mode, you would have to use 'simulated'
			// full-screen - requires Sun/Oracle JDK
			test.setBackground(new Color(0, 0, 0, 0)); // This is what you do in
														// JDK7

			// mediaPlayer.setOverlay(test);
			// mediaPlayer.enableOverlay(true);
		}

		// This might be useful
		// enableMousePointer(false);
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
						//videoSurface.setSize(dimension);
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

			// Demo the marquee
			mediaPlayer.setMarqueeText("vlcj java bindings for vlc");
			mediaPlayer.setMarqueeSize(40);
			mediaPlayer.setMarqueeOpacity(95);
			mediaPlayer.setMarqueeColour(Color.white);
			mediaPlayer.setMarqueeTimeout(5000);
			mediaPlayer.setMarqueeLocation(50, 120);
			mediaPlayer.enableMarquee(true);

			// Not quite sure how crop geometry is supposed to work...
			//
			// Assertions in libvlc code:
			//
			// top + height must be less than visible height
			// left + width must be less than visible width
			//
			// With DVD source material:
			//
			// Reported size is 1024x576 - this is what libvlc reports when you
			// call
			// get video size
			//
			// mpeg size is 720x576 - this is what is reported in the native log
			//
			// The crop geometry relates to the mpeg size, not the size reported
			// through the API
			//
			// For 720x576, attempting to set geometry to anything bigger than
			// 719x575 results in the assertion failures above (seems like it
			// should
			// allow 720x576) to me

			// mediaPlayer.setCropGeometry("4:3");
		}

		@Override
		public void error(MediaPlayer mediaPlayer) {
			logger.debug("error(mediaPlayer={})", mediaPlayer);
		}

		@Override
		public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
			logger.debug("mediaSubItemAdded(mediaPlayer={},subItem={})", mediaPlayer, subItem);
		}

		@Override
		public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
			logger.debug("mediaDurationChanged(mediaPlayer={},newDuration={})", mediaPlayer, newDuration);
		}

		@Override
		public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
			logger.debug("mediaParsedChanged(mediaPlayer={},newStatus={})", mediaPlayer, newStatus);
		}

		@Override
		public void mediaFreed(MediaPlayer mediaPlayer) {
			logger.debug("mediaFreed(mediaPlayer={})", mediaPlayer);
		}

		@Override
		public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
			logger.debug("mediaStateChanged(mediaPlayer={},newState={})", mediaPlayer, newState);
		}

		@Override
		public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
			logger.debug("mediaMetaChanged(mediaPlayer={},metaType={})", mediaPlayer, metaType);
		}
	}

	/**
	 *
	 *
	 * @param enable
	 */
	@SuppressWarnings("unused")
	private void enableMousePointer(boolean enable) {
		logger.debug("enableMousePointer(enable={})", enable);
		if (enable) {
			videoSurface.setCursor(null);
		} else {
			Image blankImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			videoSurface.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(blankImage, new Point(0, 0), ""));
		}
	}

	/**
	 *
	 */
	private final class VideoPlayerMouseListener extends MouseAdapter {
		@Override
		public void mouseMoved(MouseEvent e) {
			logger.trace("mouseMoved(e={})", e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			logger.debug("mousePressed(e={})", e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			logger.debug("mouseReleased(e={})", e);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			logger.debug("mouseClicked(e={})", e);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			logger.debug("mouseWheelMoved(e={})", e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			logger.debug("mouseEntered(e={})", e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			logger.debug("mouseExited(e={})", e);
		}
	}

	/**
	 *
	 */
	private final class VideoPlayerKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			logger.debug("keyPressed(e={})", e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			logger.debug("keyReleased(e={})", e);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			logger.debug("keyTyped(e={})", e);
		}
	}
}
