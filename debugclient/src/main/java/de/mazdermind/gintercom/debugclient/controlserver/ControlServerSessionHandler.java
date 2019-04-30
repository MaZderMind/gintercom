package de.mazdermind.gintercom.debugclient.controlserver;

import java.lang.reflect.Type;

import javax.annotation.Nonnull;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import de.mazdermind.gintercom.shared.controlserver.model.Message;

public class ControlServerSessionHandler implements StompSessionHandler {
	@Override
	public void afterConnected(@Nonnull StompSession stompSession, @Nonnull StompHeaders stompHeaders) {

	}

	@Override
	public void handleException(@Nonnull StompSession stompSession, StompCommand stompCommand, @Nonnull StompHeaders stompHeaders, @Nonnull byte[] bytes, @Nonnull Throwable throwable) {

	}

	@Override
	public void handleTransportError(@Nonnull StompSession stompSession, @Nonnull Throwable throwable) {

	}

	@Override
	@Nonnull
	public Type getPayloadType(@Nonnull StompHeaders stompHeaders) {
		return Message.class;
	}

	@Override
	public void handleFrame(@Nonnull StompHeaders stompHeaders, Object o) {
		Message message = (Message) o;

	}
}
