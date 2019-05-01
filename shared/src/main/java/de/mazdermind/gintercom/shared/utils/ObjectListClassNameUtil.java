package de.mazdermind.gintercom.shared.utils;

import java.util.List;
import java.util.stream.Collectors;

public class ObjectListClassNameUtil {
	public static List<String> classNames(List<?> objects) {
		return objects.stream()
			.map(o -> o.getClass().getSimpleName())
			.collect(Collectors.toList());
	}

	public static String classNamesList(List<?> objects) {
		return objects.stream()
			.map(o -> o.getClass().getSimpleName())
			.collect(Collectors.joining(", "));
	}
}
