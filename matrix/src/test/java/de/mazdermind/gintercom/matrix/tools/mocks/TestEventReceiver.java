package de.mazdermind.gintercom.matrix.tools.mocks;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestEventReceiver {
	private final BlockingQueue<Object> receivedEvents = new LinkedBlockingDeque<>();

	@EventListener
	@Order(Ordered.LOWEST_PRECEDENCE)
	public void handleEvent(Object event) {
		if (event.getClass().getPackage().getName().startsWith("de.mazdermind.gintercom")) {
			log.info("Received {}", event);
			receivedEvents.add(event);
		}
	}

	@SneakyThrows
	public <T> T awaitEvent(Class<T> eventType) {
		Object result = receivedEvents.poll(1, TimeUnit.SECONDS);

		assertThat(result)
			.describedAs("Expected to receive Event")
			.isNotNull();

		assertThat(result)
			.describedAs("Expected to receive Event of Type %s", eventType.getSimpleName())
			.isInstanceOf(eventType);

		//noinspection unchecked
		return (T) result;
	}

	@SneakyThrows
	public <T> Optional<T> maybeAwaitEvent(Class<T> eventType) {
		Object result = receivedEvents.poll(1, TimeUnit.SECONDS);
		if (result != null) {
			if (eventType.isInstance(result)) {
				//noinspection unchecked
				return Optional.of((T) result);
			} else {
				receivedEvents.add(result);
			}
		}

		return Optional.empty();
	}

	public void assertNoMoreEvents() {
		assertThat(receivedEvents).isEmpty();
	}

	public void clear() {
		receivedEvents.clear();
	}
}
