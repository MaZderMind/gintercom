package de.mazdermind.gintercom.testutils;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moandjiezana.toml.Toml;

public class JsonMapUtils {
	private static final ObjectMapper objectMapper;

	private static final Toml toml;

	static {
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

		toml = new Toml();
	}

	public static <T> T convertJsonTo(Class<T> klazz, Map<String, ?> map) {
		return objectMapper.convertValue(map, klazz);
	}

	public static JsonMap readTomlToMap(String file) {
		InputStream stream = JsonMapUtils.class.getClassLoader().getResourceAsStream(file);
		Map<String, Object> map = toml.read(Objects.requireNonNull(stream)).toMap();
		return new JsonMap(map);
	}

	public static <T> JsonMap convertToJson(T object) {
		//noinspection unchecked
		return new JsonMap(objectMapper.convertValue(object, Map.class));
	}

}
