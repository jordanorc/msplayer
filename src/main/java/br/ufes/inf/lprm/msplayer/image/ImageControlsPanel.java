package br.ufes.inf.lprm.msplayer.image;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.stream.StreamResult;

import br.ufes.inf.lprm.msplayer.audio.AudioExtractor;
import br.ufes.inf.lprm.msplayer.audio.AudioInfo;
import br.ufes.inf.lprm.msplayer.audio.SilenceInfo;
import br.ufes.inf.lprm.msplayer.video.PlayerVideoControlsPanel;
import br.ufes.inf.lprm.msplayer.video.utils.FrameExtractor;
import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.filter.swing.SwingFileFilterFactory;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class ImageControlsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int SKIP_TIME_MS = 10 * 1000;
	private JFileChooser fileChooser;

	private JButton redButton;
	private JButton greenButton;
	private JButton blueButton;
	private JButton openButton;
	private JButton resetButton;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private ImageEditor.ImagePanel panel;

	public ImageControlsPanel(ImageEditor.ImagePanel panel) {
		this.panel = panel;
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
		createUI();
	}

	private void createUI() {
		createControls();
		layoutControls();
		registerListeners();
	}

	private void createControls() {
		openButton = new JButton();
		openButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/open.png")));
		openButton.setToolTipText("Open");

		redButton = new JButton();
		redButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/r_icon.png")));
		redButton.setToolTipText("Red");

		greenButton = new JButton();
		greenButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/g_icon.png")));
		greenButton.setToolTipText("Green");

		blueButton = new JButton();
		blueButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/b_icon.png")));
		blueButton.setToolTipText("Blue");

		resetButton = new JButton();
		resetButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/reset.png")));
		resetButton.setToolTipText("Play");

		zoomInButton = new JButton();
		zoomInButton
				.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/zoom_in.png")));
		zoomInButton.setToolTipText("Skip forward");

		zoomOutButton = new JButton();
		zoomOutButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/zoom_out.png")));
		zoomOutButton.setToolTipText("Go to next chapter");
	}

	private void layoutControls() {
		setBorder(new EmptyBorder(4, 4, 4, 4));

		setLayout(new BorderLayout());

		JPanel bottomPanel = new JPanel();

		bottomPanel.setLayout(new FlowLayout());

		bottomPanel.add(openButton);
		bottomPanel.add(redButton);
		bottomPanel.add(greenButton);
		bottomPanel.add(blueButton);

		bottomPanel.add(resetButton);
		bottomPanel.add(zoomInButton);
		bottomPanel.add(zoomOutButton);

		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void registerListeners() {
		redButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = panel.getImage();
				BufferedImage output = RGBConverter.to(image, RGB.RED);
				panel.loadImage(output);
			}
		});

		greenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = panel.getImage();
				BufferedImage output = RGBConverter.to(image, RGB.GREEN);
				panel.loadImage(output);
			}
		});

		blueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = panel.getImage();
				BufferedImage output = RGBConverter.to(image, RGB.BLUE);
				panel.loadImage(output);
			}
		});
		
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.reset();
			}
		});
		
		zoomInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.zoomIn();
			}
		});
		
		zoomOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.zoomOut();
			}
		});
			
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.reset();
			}
		});

		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(ImageControlsPanel.this)) {
					panel.setImage(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
	}

}
