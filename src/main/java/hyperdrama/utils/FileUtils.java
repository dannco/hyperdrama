package hyperdrama.utils;

import hyperdrama.Runner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileUtils {

	public static void writeToFile(String file, String content) {
		Runner.log(String.format("saving %s", file));
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(content.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
