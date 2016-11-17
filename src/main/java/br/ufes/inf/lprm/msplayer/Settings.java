package br.ufes.inf.lprm.msplayer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Settings {

	private static final Settings INSTANCE = new Settings();
	private static Path TEMP_PATH;
	private static Path FRAMES_PATH;
	private static Path AUDIO_PATH;

	private Settings() {
		if (INSTANCE != null) {
			throw new IllegalStateException("Already instantiated");
		}

		try {
			TEMP_PATH = Files.createTempDirectory("");
			recursiveDeleteOnShutdownHook(TEMP_PATH);

			FRAMES_PATH = Files.createDirectory(Paths.get(TEMP_PATH.toAbsolutePath().toString(), "frames"));
			AUDIO_PATH = Files.createDirectory(Paths.get(TEMP_PATH.toAbsolutePath().toString(), "audio"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void recursiveDeleteOnShutdownHook(final Path path) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file,
								@SuppressWarnings("unused") BasicFileAttributes attrs) throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
							if (e == null) {
								Files.delete(dir);
								return FileVisitResult.CONTINUE;
							}
							// directory iteration failed
							throw e;
						}
					});
				} catch (IOException e) {
					throw new RuntimeException("Failed to delete " + path, e);
				}
			}
		}));
	}

	public Path getTempPath() {
		return TEMP_PATH;
	}

	public Path getFramesPath() {
		return FRAMES_PATH;
	}

	public Path getAudioPath() {
		return AUDIO_PATH;
	}

	public static Settings getInstance() {
		return INSTANCE;
	}

	@Override
	public Settings clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone instance of this class");
	}

}
