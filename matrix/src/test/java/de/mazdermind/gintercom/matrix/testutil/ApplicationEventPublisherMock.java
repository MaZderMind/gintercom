package de.mazdermind.gintercom.matrix.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

public class ApplicationEventPublisherMock implements ApplicationEventPublisher {
	private List<Object> publishedEvents = new ArrayList<>();

	@Override
	public void publishEvent(@NonNull Object o) {
		this.publishedEvents.add(o);
	}

	public List<Object> getPublishedEvents() {
		return Collections.unmodifiableList(publishedEvents);
	}

	public void assertEventTypes(Class<?>... classes) {
		List<Class<?>> actualClasses = publishedEvents.stream().map(Object::getClass).collect(Collectors.toList());
		assertThat(actualClasses).containsExactlyInAnyOrder(classes);
	}

	public void assertNoEventsFirered() {
		assertThat(publishedEvents).isEmpty();
	}

	public <T> T getEvent(int index) {
		//noinspection unchecked
		return (T) publishedEvents.get(index);
	}

	public <T> List<T> getEvents(Class<T> type) {
		//noinspection unchecked
		return (List<T>) publishedEvents.stream()
			.filter(type::isInstance)
			.collect(Collectors.toList());
	}

	public <T> T getEvent(Class<T> type) {
		return getEvents(type).get(0);
	}

	public <T> T getEvents(Class<T> type, int index) {
		return getEvents(type).get(index);
	}
}
