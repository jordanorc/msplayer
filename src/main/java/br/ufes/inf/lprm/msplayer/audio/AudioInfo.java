package br.ufes.inf.lprm.msplayer.audio;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class AudioInfo {
	private static final int NUM_BITS_PER_BYTE = 8;

	private AudioInputStream audioInputStream;
	private int[][] samplesContainer;

	// cached values
	protected int sampleMax = 0;
	protected int sampleMin = 0;
	protected double biggestSample;

	public AudioInfo(AudioInputStream aiStream) {
		this.audioInputStream = aiStream;
		createSampleArrayCollection();
	}

	public int getNumberOfChannels() {
		int numBytesPerSample = audioInputStream.getFormat().getSampleSizeInBits() / NUM_BITS_PER_BYTE;
		return audioInputStream.getFormat().getFrameSize() / numBytesPerSample;
	}

	private void createSampleArrayCollection() {
		try {
			audioInputStream.mark(Integer.MAX_VALUE);
			audioInputStream.reset();
			byte[] bytes = new byte[(int) (audioInputStream.getFrameLength())
					* ((int) audioInputStream.getFormat().getFrameSize())];
			int result = 0;
			try {
				result = audioInputStream.read(bytes);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// convert sample bytes to channel separated 16 bit samples
			samplesContainer = getSampleArray(bytes);

			// find biggest sample. used for interpolating the yScaleFactor
			if (sampleMax > sampleMin) {
				biggestSample = sampleMax;
			} else {
				biggestSample = Math.abs(((double) sampleMin));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected int[][] getSampleArray(byte[] eightBitByteArray) {
		int[][] toReturn = new int[getNumberOfChannels()][eightBitByteArray.length / (2 * getNumberOfChannels())];
		int index = 0;

		// loop through the byte[]
		for (int t = 0; t < eightBitByteArray.length;) {
			// for each iteration, loop through the channels
			for (int a = 0; a < getNumberOfChannels(); a++) {
				// do the byte to sample conversion
				// see AmplitudeEditor for more info
				int low = (int) eightBitByteArray[t];
				t++;
				int high = (int) eightBitByteArray[t];
				t++;
				int sample = (high << 8) + (low & 0x00ff);

				if (sample < sampleMin) {
					sampleMin = sample;
				} else if (sample > sampleMax) {
					sampleMax = sample;
				}
				// set the value.
				toReturn[a][index] = sample;
			}
			index++;
		}

		return toReturn;
	}

	public double getXScaleFactor(int panelWidth) {
		return (panelWidth / ((double) samplesContainer[0].length));
	}

	public double getYScaleFactor(int panelHeight) {
		return (panelHeight / (biggestSample * 2 * 1.2));
	}

	public int[] getAudio(int channel) {
		return samplesContainer[channel];
	}

	protected int getIncrement(double xScale) {
		try {
			int increment = (int) (samplesContainer[0].length / (samplesContainer[0].length * xScale));
			return increment;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public ArrayList<SilenceInfo> findSilence(double secs, double db) {
		AudioFormat format = audioInputStream.getFormat();
		int[] samples = getAudio(0);

		ArrayList<SilenceInfo> silenceInfo = new ArrayList<SilenceInfo>();

		// 22050 bytes equivalem a 0.500 segundos
		double thresholdInSecs = secs;
		double maxDb = db;
		double byteRate = thresholdInSecs * format.getSampleRate();
		SilenceInfo silence = new SilenceInfo(-1);
		int counter = 0;
		try {
			if (samples != null) {
				for (int i = 0; i < samples.length; i++) {
					if (counter < byteRate) {
						if (Math.abs(samples[i]) >= 0 && Math.abs(samples[i]) <= maxDb) {
							counter = counter + 1;
						} else {
							if (silence.GetStart() != -1) {
								silence.SetEnd(i);
								silence.CalculateDuration(format.getSampleRate());
								silenceInfo.add(silence);

								silence = new SilenceInfo(-1);
							}
							counter = 0;
						}
					} else {
						if (silence.GetStart() == -1)
							silence.SetStart(i - counter);
						silence.SetEnd(i);
						counter = 0;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return silenceInfo;
	}

	public String createXML(ArrayList<SilenceInfo> silenceInfo) {
		String xml = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();

			Element rootElement = doc.createElement("SilenceInfo");
			doc.appendChild(rootElement);

			for (int i = 0; i < silenceInfo.size(); i++) {

				Element row = doc.createElement("Row");
				rootElement.appendChild(row);
				row.setAttribute("id", Integer.toString(i));

				String info = Float.toString(silenceInfo.get(i).GetStart());

				Element start = doc.createElement("start");
				start.appendChild(doc.createTextNode(info));
				row.appendChild(start);

				info = Float.toString(silenceInfo.get(i).GetEnd());
				Element end = doc.createElement("end");
				end.appendChild(doc.createTextNode(info));
				row.appendChild(end);

				info = Float.toString(silenceInfo.get(i).GetDuration());
				Element duration = doc.createElement("duration");
				duration.appendChild(doc.createTextNode(info));
				row.appendChild(duration);
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			StringWriter writer = new StringWriter();
			transformer.transform(source, new StreamResult(writer));

			xml = writer.getBuffer().toString();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		return xml;
	}

}