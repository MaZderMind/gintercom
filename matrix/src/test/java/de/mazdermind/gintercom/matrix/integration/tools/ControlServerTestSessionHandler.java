package de.mazdermind.gintercom.matrix.integration.tools;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class ControlServerTestSessionHandler extends StompSessionHandlerAdapter {
	private static Logger log = LoggerFactory.getLogger(ControlServerTestSessionHandler.class);

	private List<Throwable> errors = new ArrayList<>();
	private List<StompMessage> messages = new ArrayList<>();

	private AtomicReference<FutureMessage> futureMessage = new AtomicReference<>();

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Map.class;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		log.info("Received Frame for {}", headers.getDestination());

		FutureMessage futureMessage = this.futureMessage.get();
		if (futureMessage != null && futureMessage.getDestination().equals(headers.getDestination())) {
			log.info("Completing waiting Future for this Destination");
			futureMessage.getFuture().complete(payload);
			return;
		}

		messages.add(new StompMessage(headers.getDestination(), payload));
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
		log.warn("Handling Exception: {}", exception.getMessage());
		errors.add(exception);
	}

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		log.warn("Handling Transport-Error: {}", exception.getMessage());
		errors.add(exception);
	}

	public List<Throwable> getErrors() {
		return errors;
	}

	public List<StompMessage> getMessages() {
		return messages;
	}

	public Object awaitMessage(String destination, Duration timeout) {
		Optional<StompMessage> alreadyReceivedMessage = messages.stream()
			.filter(stompMessage -> stompMessage.getDestination().equals(destination)).findFirst();

		if (alreadyReceivedMessage.isPresent()) {
			messages.remove(alreadyReceivedMessage.get());
			return alreadyReceivedMessage.get().getPayload();
		}

		futureMessage.set(new FutureMessage(destination));
		try {
			return futureMessage.get().getFuture().get(timeout.toMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			//
		}
		futureMessage.set(null);
		throw new AssertionError(String.format("Expected Message to %s has not been received within the Timeout %s", destination, timeout));
	}

}
