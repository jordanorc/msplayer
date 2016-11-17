package br.ufes.inf.lprm.msplayer.audio;

public class SilenceInfo {
	private float duration = 0;
	private float start = 0;
	private float end = 0;
	
	private int startIndex;
	private int endIndex;
	
	public int GetStartIndex(){
		return this.startIndex;
	}
	
	public int GetEndIndex(){
		return this.endIndex;
	}
	
	public void SetStart(float start){
		this.start = start;
		this.startIndex = (int)start;
	}
	
	public float GetStart(){
		return this.start;
	}
	
	public void SetEnd(float end){
		this.end = end;
		this.endIndex = (int)end;
	}
	
	public float GetEnd(){
		return this.end;
	}
	
	public float GetDuration(){
		return this.duration;
	}
	
	public SilenceInfo(float start){
		this.start = start;
		this.startIndex = (int)start;
	}
	
	public SilenceInfo(float start, float end){
		this.start = start;
		this.end = end;
		this.startIndex = (int)start;
		this.endIndex = (int)end;
	}
	
	public void CalculateDuration(double SampleRate){
		this.start = this.start/(float)SampleRate;
		this.end = this.end/(float)SampleRate;
		this.duration = (this.end - this.start);
	}
}
