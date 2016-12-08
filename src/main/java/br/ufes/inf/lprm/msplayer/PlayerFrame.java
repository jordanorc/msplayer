package br.ufes.inf.lprm.msplayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.ToolTipManager;

import br.ufes.inf.lprm.msplayer.image.ImageEditor;
import br.ufes.inf.lprm.msplayer.video.PlayerVideo;

public class PlayerFrame extends JFrame {
	
	JMenuItem imageEditorMenuItem;
	JMenuItem mediaExitMenuItem;

	@Override
	protected void frameInit() {
		super.frameInit();
		this.initialize();
	}

	public void initialize() {
		this.setIconImage(new ImageIcon(getClass().getResource("/icons/vlcj-logo.png")).getImage());

		setLayout(new BorderLayout());
		setBackground(Color.black);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PlayerVideo videoPlayer = new PlayerVideo(this);
		add(videoPlayer);
		videoPlayer.setVisible(true);	
		pack();
		
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		setJMenuBar(buildMenuBar());
		
		videoPlayer.invalidate();
		videoPlayer.revalidate();
		invalidate();
		revalidate();
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

		/*JMenuItem mediaPlayStreamMenuItem = new JMenuItem("Play Stream...");
		mediaPlayFileMenuItem.setMnemonic('s');
		mediaMenu.add(mediaPlayStreamMenuItem);*/

		mediaMenu.add(new JSeparator());

		mediaExitMenuItem = new JMenuItem("Exit");
		mediaExitMenuItem.setMnemonic('x');
		mediaMenu.add(mediaExitMenuItem);

		menuBar.add(mediaMenu);

		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('t');

		imageEditorMenuItem = new JMenuItem("Image Editor");
		imageEditorMenuItem.setMnemonic('e');
		toolsMenu.add(imageEditorMenuItem);

		menuBar.add(toolsMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('h');

		JMenuItem helpAboutMenuItem = new JMenuItem("About...");
		helpAboutMenuItem.setMnemonic('a');
		helpMenu.add(helpAboutMenuItem);

		menuBar.add(helpMenu);
		
		registerListeners();

		return menuBar;
	}
	
	private void registerListeners() {
		imageEditorMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageEditor editor = new ImageEditor();
				editor.setVisible(true);				
			}
		});
		
		mediaExitMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);		
			}
		});
	}
}
