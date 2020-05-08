package de.mazdermind.gintercom.clientsupport.utils;

import java.util.Collection;
import java.util.stream.Collectors;

public class ObjectListClassNameUtil {
	public static String classNamesList(Collection<?> objects) {
		return objects.stream()
			.map(o -> o.getClass().getSimpleName())
			.collect(Collectors.joining(", "));
	}
}
