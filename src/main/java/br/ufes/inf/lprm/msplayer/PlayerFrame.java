package br.ufes.inf.lprm.msplayer;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import br.ufes.inf.lprm.msplayer.video.PlayerVideo;

public class PlayerFrame extends JFrame {

	@Override
	protected void frameInit() {
		super.frameInit();
		this.initialize();
	}

	public void initialize() {
		this.setIconImage(new ImageIcon(getClass().getResource("/icons/vlcj-logo.png")).getImage());

		setLayout(new BorderLayout());
		setBackground(Color.black);
		setJMenuBar(buildMenuBar());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PlayerVideo videoPlayer = new PlayerVideo(this);
		add(videoPlayer);
		videoPlayer.setVisible(true);
		this.pack();
	}

	private JMenuBar buildMenuBar() {
		// Menus are just added as an example of overlapping the video - they
		// are
		// non-functional in this demo player

		JMenuBar menuBar = new JMenuBar();

		JMenu mediaMenu = new JMenu("Media");
		mediaMenu.setMnemonic('m');

		JMenuItem mediaPlayFileMenuItem = new JMenuItem("Play File...");
		mediaPlayFileMenuItem.setMnemonic('f');
		mediaMenu.add(mediaPlayFileMenuItem);

		JMenuItem mediaPlayStreamMenuItem = new JMenuItem("Play Stream...");
		mediaPlayFileMenuItem.setMnemonic('s');
		mediaMenu.add(mediaPlayStreamMenuItem);

		mediaMenu.add(new JSeparator());

		JMenuItem mediaExitMenuItem = new JMenuItem("Exit");
		mediaExitMenuItem.setMnemonic('x');
		mediaMenu.add(mediaExitMenuItem);

		menuBar.add(mediaMenu);

		JMenu playbackMenu = new JMenu("Playback");
		playbackMenu.setMnemonic('p');

		JMenu playbackChapterMenu = new JMenu("Chapter");
		playbackChapterMenu.setMnemonic('c');
		for (int i = 1; i <= 25; i++) {
			JMenuItem chapterMenuItem = new JMenuItem("Chapter " + i);
			playbackChapterMenu.add(chapterMenuItem);
		}
		playbackMenu.add(playbackChapterMenu);

		JMenu subtitlesMenu = new JMenu("Subtitles");
		playbackChapterMenu.setMnemonic('s');
		String[] subs = { "01 English (en)", "02 English Commentary (en)", "03 French (fr)", "04 Spanish (es)",
				"05 German (de)", "06 Italian (it)" };
		for (int i = 0; i < subs.length; i++) {
			JMenuItem subtitlesMenuItem = new JMenuItem(subs[i]);
			subtitlesMenu.add(subtitlesMenuItem);
		}
		playbackMenu.add(subtitlesMenu);

		menuBar.add(playbackMenu);

		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('t');

		JMenuItem toolsPreferencesMenuItem = new JMenuItem("Preferences...");
		toolsPreferencesMenuItem.setMnemonic('p');
		toolsMenu.add(toolsPreferencesMenuItem);

		menuBar.add(toolsMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('h');

		JMenuItem helpAboutMenuItem = new JMenuItem("About...");
		helpAboutMenuItem.setMnemonic('a');
		helpMenu.add(helpAboutMenuItem);

		menuBar.add(helpMenu);

		return menuBar;
	}
}
