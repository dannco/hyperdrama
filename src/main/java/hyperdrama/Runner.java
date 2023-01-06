package hyperdrama;

import hyperdrama.models.Input;
import hyperdrama.models.Output;
import hyperdrama.utils.FileUtils;
import hyperdrama.utils.Reader;

import java.io.File;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class Runner {
	static List<String> log = new ArrayList<>();

	public static void log(String message) {
		System.out.println(message);
		log.add(message);
	}

	public static void main(String[] args) {
		// read project files based on input argument
		String inputFolder = args.length == 0 ? "" : args[0];
		if (inputFolder.isEmpty()) {
			System.out.println("empty argument list");
			return;
		}
		File dir = new File(inputFolder);
		if (!dir.exists() || !dir.isDirectory()) {
			System.out.println("not an existing folder");
			return;
		}

		List<Input> inputs = new ArrayList<>();
		Optional.ofNullable(dir.listFiles()).ifPresent(arr -> Stream.of(arr).map(file -> {
			Input in = Reader.getStringAsObject(Reader.getFileContents(file.getAbsolutePath()),
					Input.class);
			if (Objects.nonNull(in)) {
				in.setFileName(file.getName());
			}
			return in;
		}).filter(Objects::nonNull).forEach(inputs::add));
		if (inputs.isEmpty())
			log("WARNING: no input files read");
		Output result = inputs.stream().reduce(new Input(), (Input::merge)).asOutput();
		result.setFileName(String.format("%s_result", inputFolder));
		result.process();
		FileUtils.writeToFile("log.txt", String.join("\n", log));
	}

}
