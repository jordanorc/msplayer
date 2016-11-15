package br.ufes.inf.lprm.msplayer.video.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.ufes.inf.lprm.msplayer.Settings;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class FrameExtractor {

	String mediaPath;

	public FrameExtractor(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	public List<String> getFrames() {
		List<String> frames = new ArrayList<String>();
		CustomFFprobe ffprobe1;
		try {
			ffprobe1 = new CustomFFprobe();

			String data = ffprobe1.run("-v", "quiet", "-select_streams", "v", "-show_frames", "-show_entries",
					"frame=pict_type", "-print_format", "json", mediaPath);
			JsonObject obj = new JsonParser().parse(data).getAsJsonObject();

			JsonArray farray = obj.get("frames").getAsJsonArray();
			for (int i = 0; i < farray.size(); i++) {
				JsonObject object = farray.get(i).getAsJsonObject();
				System.out.println("Frame: " + i + ": " + object.get("pict_type").getAsString());
				frames.add(object.get("pict_type").getAsString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return frames;
	}

	public void getKeyFrames() {
		try {
			FFmpeg ffmpeg = new FFmpeg();
			FFprobe ffprobe = new FFprobe();

			FFmpegBuilder builder = new FFmpegBuilder().setInput(mediaPath).addExtraArgs("-vsync", "2")
					.addOutput(
							Settings.getInstance().getFramesPath().toAbsolutePath().toString() + "/%04d_thumbnail.jpeg")
					.setFormat("image2").setVideoFilter("select=eq(pict_type\\,PICT_TYPE_I)").done();

			FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
			// Run a one-pass encode
			executor.createJob(builder).run();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
