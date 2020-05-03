package de.mazdermind.gintercom.testutils.captors;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.mockito.ArgumentCaptor;

public class FilteringArgumentCaptor<T> {

	private final Class<T> clazz;
	private final ArgumentCaptor<T> argumentCaptor;

	private FilteringArgumentCaptor(Class<T> clazz, ArgumentCaptor<T> argumentCaptor) {
		this.clazz = clazz;
		this.argumentCaptor = argumentCaptor;
	}

	public static <T> FilteringArgumentCaptor<T> forClass(Class<T> clazz) {
		return new FilteringArgumentCaptor<T>(
			clazz,
			ArgumentCaptor.forClass(clazz)
		);
	}

	public T getValue() {
		return this.argumentCaptor.getAllValues().stream()
			.filter(clazz::isInstance)
			.findFirst()
			.orElseThrow(NoSuchElementException::new);
	}

	public List<T> getMatchingValues() {
		return this.argumentCaptor.getAllValues().stream()
			.filter(clazz::isInstance)
			.collect(Collectors.toList());
	}

	public List<Object> getAllValues() {
		//noinspection unchecked
		return (List<Object>) this.argumentCaptor.getAllValues();
	}

	public T capture() {
		return argumentCaptor.capture();
	}
}
