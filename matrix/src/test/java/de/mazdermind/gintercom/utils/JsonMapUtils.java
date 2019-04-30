package de.mazdermind.gintercom.utils;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moandjiezana.toml.Toml;

public class JsonMapUtils {
	private static ObjectMapper objectMapper;

	private static Toml toml;

	static {
		objectMapper = new ObjectMapper();
		objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

		toml = new Toml();
	}

	public static Map<String, Object> getJsonMap(Map<String, ?> parent, String key) {
		//noinspection unchecked
		return (Map<String, Object>) parent.get(key);
	}

	public static <T> T convertJsonTo(Class<T> klazz, Map<String, ?> map) {
		return objectMapper.convertValue(map, klazz);
	}

	public static Map<String, Object> readTomlToMap(String file) {
		InputStream stream = JsonMapUtils.class.getClassLoader().getResourceAsStream(file);
		return toml.read(Objects.requireNonNull(stream)).toMap();
	}
}
