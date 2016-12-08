package br.ufes.inf.lprm.msplayer.video;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import br.ufes.inf.lprm.msplayer.Settings;
import br.ufes.inf.lprm.msplayer.image.ImageEditor;
import br.ufes.inf.lprm.msplayer.video.utils.FrameExtractor;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class PlayerVideoFramePanel extends JScrollPane {

	private final MediaPlayer mediaPlayer;
	private static final long serialVersionUID = 1L;
	private final static JPanel panelKeyFrames = new JPanel();
	private JLabel loading;

	public PlayerVideoFramePanel(MediaPlayer mediaPlayer) {
		super(panelKeyFrames);
		this.mediaPlayer = mediaPlayer;
		createUI();
	}

	private File[] getFrameFiles() {
		File[] files = Settings.getInstance().getFramesPath().toFile().listFiles((FileFilter) FileFileFilter.FILE);
		Arrays.sort(files, NameFileComparator.NAME_COMPARATOR);
		return files;
	}

	private List<JLabel> getFrames() {
		File[] files = getFrameFiles();
		List<JLabel> frames = new ArrayList<JLabel>();

		try {
			for (int i = 0; i < files.length; i++) {
				JLabel thumb = new JLabel();
				BufferedImage img = ImageIO.read(files[i]);
				BufferedImage scaledImg = Scalr.resize(img, Method.QUALITY, 200, 80);
				ImageIcon imageIcon = new ImageIcon(scaledImg);
				thumb.setIcon(imageIcon);
				// thumb.setText(files[i].getName());
				thumb.setBorder(new EmptyBorder(4, 4, 4, 4));
				frames.add(i, thumb);
				
				thumb.addMouseListener(new MouseAdapter(){
				    @Override
				    public void mouseClicked(MouseEvent e){
				        if(e.getClickCount()==2){
				        	ImageEditor editor = new ImageEditor(img);
				        	editor.setVisible(true);
				        }
				    }
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return frames;
	}

	private void updateFrames() {
		List<JLabel> frames = getFrames();
		loading.setVisible(false);
		for (JLabel pic : frames) {
			panelKeyFrames.add(pic);
		}

		panelKeyFrames.invalidate();
		panelKeyFrames.revalidate();
		repaint();
	}

	private void createUI() {
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void opening(MediaPlayer mediaPlayer) {
				String mediaUrl = mediaPlayer.mrl();
				loading.setVisible(true);
				FrameExtractor extractor = new FrameExtractor(mediaUrl);
				extractor.getKeyFrames();
				updateFrames();

			}
		});

		panelKeyFrames.setLayout(new FlowLayout());

		JPanel panelLoading = new JPanel();

		ImageIcon loadingIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/loading.gif"));
		loading = new JLabel(loadingIcon, JLabel.CENTER);
		loading.setBorder(new EmptyBorder(8, 8, 8, 8));
		panelLoading.add(loading);
		loading.setVisible(false);

		// panelLoading.setSize(400, 300);
		panelLoading.setVisible(true);
		panelKeyFrames.add(panelLoading);

		Dimension d = getPreferredSize();
		d.height = 200;
		setPreferredSize(d);

		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		updateFrames();
	}
}
