package br.ufes.inf.lprm.msplayer.audio;

public class DrawnSample {
	public int pos;
    public double scaledSample;
    public int y;
    public int x;
    
    public DrawnSample(int oldX, int oldY, int xIndex, int y){
    	this.pos = xIndex;
    	this.x = oldX;
    	this.y = oldY;
    	this.scaledSample = y;
    }
    
    public DrawnSample(int pos, double scaledSample, int y){
    	this.pos = pos;
    	this.scaledSample = scaledSample;
    	this.y = y;
    }
}
