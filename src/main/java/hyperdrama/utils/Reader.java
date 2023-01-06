package hyperdrama.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Reader {
	public static String getFileContents(String pathToFile) {
		try (
				FileInputStream fis = new FileInputStream(pathToFile);
				InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
				BufferedReader br = new BufferedReader(reader)
		) {
			return br.lines().collect(Collectors.joining());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	private static ObjectMapper mapper = new ObjectMapper();

	public static <T> T getStringAsObject(
			String input, Class<T> clazz
	) {
		try {
			return mapper.readValue(input, clazz);
		} catch (JsonProcessingException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}

	}
}
