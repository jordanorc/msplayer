package br.ufes.inf.lprm.msplayer.image;

public enum RGB {
	RED(16), GREEN(8), BLUE(0);

	private int bits;

	private RGB(int bits) {
		this.bits = bits;
	}

	public int to(int color) {
		int to = 0;
		switch (this) {
			case BLUE:
		        int ba = (color>>24)&0xff;
		        int b = color&0xff;
		        //set new RGB
		    	to = (ba<<24) | (0<<16) | (0<<8) | b;
		    	break;
			case GREEN:
		        int ga = (color>>24)&0xff;
		        int g = (color>>8)&0xff;
		        
		        //set new RGB
		        to = (ga<<24) | (0<<16) | (g<<8) | 0;
		        break;
			case RED:
                int ra = (color>>24)&0xff;
                int r = (color>>16)&0xff;
                
                //set new RGB
            	to = (ra<<24) | (r<<16) | (0<<8) | 0;
            	break;
		}
		return to;
	}
}