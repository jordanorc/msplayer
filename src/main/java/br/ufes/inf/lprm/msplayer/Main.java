package br.ufes.inf.lprm.msplayer;

import javax.swing.SwingUtilities;

import br.ufes.inf.lprm.msplayer.video.PlayerVideo;

public class Main {
	
	static PlayerFrame player = new PlayerFrame();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	player.setVisible(true);
            }
        });

	}
}
