package de.mazdermind.gintercom.matrix.tools.mocks;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;

@Component
public class TestEventReceiver {
	private final BlockingQueue<Object> receivedEvents = new LinkedBlockingDeque<>();

	@EventListener
	public void handleEvent(Object event) {
		if (event.getClass().getPackage().getName().startsWith("de.mazdermind.gintercom")) {
			receivedEvents.add(event);
		}
	}

	@SneakyThrows
	public <T> T awaitEvent(Class<T> eventType) {
		Object result = receivedEvents.poll(1, TimeUnit.SECONDS);

		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(eventType);

		//noinspection unchecked
		return (T) result;
	}

	public void assertNoMoreEvents() {
		assertThat(receivedEvents).isEmpty();
	}
}
