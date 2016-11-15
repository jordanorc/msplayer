package br.ufes.inf.lprm.msplayer.video.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

import com.google.common.base.MoreObjects;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.ProcessFunction;
import net.bramp.ffmpeg.RunProcessFunction;

public class CustomFFprobe extends FFprobe {

	final static String FFPROBE = "ffprobe";
	final static String DEFAULT_PATH = MoreObjects.firstNonNull(System.getenv("FFPROBE"), FFPROBE);
	final static ProcessFunction runFunc = new RunProcessFunction();

	public CustomFFprobe() throws IOException {
		super(DEFAULT_PATH, runFunc);
		// TODO Auto-generated constructor stub
	}

	public String run(String... args) throws IOException {
		checkNotNull(args);
		String data = "";

		Process p = runFunc.run(path(Arrays.asList(args)));
		try {
			BufferedReader r = wrapInReader(p);
			String line = "";
			while ((line = r.readLine()) != null) {
				data += line;
			}
			throwOnError(p);

		} finally {
			p.destroy();
		}
		return data;
	}

}
