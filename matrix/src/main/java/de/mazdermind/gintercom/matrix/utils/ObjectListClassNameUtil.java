package de.mazdermind.gintercom.matrix.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectListClassNameUtil {
	public static List<String> classNames(Collection<?> objects) {
		return objects.stream()
			.map(o -> o.getClass().getSimpleName())
			.collect(Collectors.toList());
	}

	public static String classNamesList(Collection<?> objects) {
		return objects.stream()
			.map(o -> o.getClass().getSimpleName())
			.collect(Collectors.joining(", "));
	}
}
