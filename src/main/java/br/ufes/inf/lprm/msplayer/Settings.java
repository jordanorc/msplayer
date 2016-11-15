package br.ufes.inf.lprm.msplayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {

	private static final Settings INSTANCE = new Settings();
	private static Path TEMP_PATH;
	private static Path FRAMES_PATH;

	private Settings() {
		if (INSTANCE != null) {
			throw new IllegalStateException("Already instantiated");
		}
		
		try {
			TEMP_PATH = Files.createTempDirectory("");
			Path path = Paths.get(TEMP_PATH.toAbsolutePath().toString(), "frames");
			FRAMES_PATH = Files.createDirectory(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Path getTempPath() {
		return TEMP_PATH;
	}
	
	public Path getFramesPath() {
		return FRAMES_PATH;
	}

	public static Settings getInstance() {
		return INSTANCE;
	}

	@Override
	public Settings clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone instance of this class");
	}

}
