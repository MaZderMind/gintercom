package de.mazdermind.gintercom.clientsupport.controlserver.connection;

import static de.mazdermind.gintercom.clientsupport.utils.ObjectListClassNameUtil.classNamesList;

import java.lang.reflect.Type;
import java.util.Collection;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientsupport.controlserver.messagehandler.MatrixMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ControlServerSessionHandler implements StompSessionHandler {
	private final ApplicationEventPublisher eventPublisher;
	private final ListableBeanFactory beanFactory;

	@Override
	public void afterConnected(@NonNull StompSession stompSession, @NonNull StompHeaders stompHeaders) {
		Collection<MatrixMessageHandler> messageHandlers = beanFactory.getBeansOfType(MatrixMessageHandler.class).values();
		log.info("Connected. Subscribing {} MessageHandlers: {}", messageHandlers.size(), classNamesList(messageHandlers));

		messageHandlers.forEach(messageHandler ->
			stompSession.subscribe(messageHandler.getDestination(), messageHandler));
	}

	@Override
	public void handleException(@NonNull StompSession stompSession, StompCommand stompCommand, @NonNull StompHeaders stompHeaders, @NonNull byte[] bytes, @NonNull Throwable throwable) {
		log.info("Exception", throwable);
	}

	@Override
	public void handleTransportError(@NonNull StompSession stompSession, @NonNull Throwable throwable) {
		log.info("TransportError: {}", throwable.getMessage());
		eventPublisher.publishEvent(new ControlServerSessionTransportErrorEvent(throwable.getMessage()));
	}

	@Override
	@NonNull
	public Type getPayloadType(@NonNull StompHeaders stompHeaders) {
		return Object.class;
	}

	@Override
	public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {
	}
}
