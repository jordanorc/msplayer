package br.ufes.inf.lprm.msplayer.audio;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;

public class SingleWaveformPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final Color BACKGROUND_COLOR = Color.gray;
	protected static final Color REFERENCE_LINE_COLOR = Color.blue;
	protected static final Color WAVEFORM_COLOR = Color.blue;
	public ArrayList<DrawnSample> drawnSamples = new ArrayList<DrawnSample>();

	private AudioInfo helper;
	private int channelIndex;
	private int[] samples;

	public SingleWaveformPanel(AudioInfo helper, int channelIndex) {
		this.helper = helper;
		this.channelIndex = channelIndex;
		setBackground(BACKGROUND_COLOR);
	}

	public int[] GetSamples() {
		return this.samples;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int lineHeight = getHeight() / 2;
		g.setColor(REFERENCE_LINE_COLOR);
		g.drawLine(0, lineHeight, (int) getWidth(), lineHeight);
		drawWaveform(g, helper.getAudio(channelIndex));
	}

	public void drawTheBeach(SilenceInfo silenceInfo) {
		DrawnSample drawnS = drawnSamples.get(silenceInfo.GetStartIndex());
		DrawnSample drawnE = drawnSamples.get(silenceInfo.GetEndIndex());
		this.getGraphics().drawRect(silenceInfo.GetStartIndex(), getHeight() / 2, 30, 30);
		this.getGraphics().drawRect(silenceInfo.GetEndIndex(), getHeight() / 2, 30, 30);
	}

	protected void drawWaveform(Graphics g, int[] samples) {
		if (samples == null) {
			return;
		} else
			this.samples = samples;

		int oldX = 0;
		int oldY = (int) (getHeight() / 2);
		int xIndex = 0;

		int increment = helper.getIncrement(helper.getXScaleFactor(getWidth()));
		g.setColor(WAVEFORM_COLOR);

		int t = 0;

		for (t = 0; t < increment; t += increment) {
			g.drawLine(oldX, oldY, xIndex, oldY);
			drawnSamples.add(new DrawnSample(oldX, oldY, xIndex, oldY));
			xIndex++;
			oldX = xIndex;
		}

		for (; t < samples.length; t += increment) {
			double scaleFactor = helper.getYScaleFactor(getHeight());
			double scaledSample = samples[t] * scaleFactor;
			int y = (int) ((getHeight() / 2) - (scaledSample));
			g.drawLine(oldX, oldY, xIndex, y);
			drawnSamples.add(new DrawnSample(oldX, oldY, xIndex, y));
			xIndex++;
			oldX = xIndex;
			oldY = y;
		}
	}

}