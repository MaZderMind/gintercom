package de.mazdermind.gintercom.shared.controlserver.connection;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.shared.controlserver.messagehandler.ProvisionMessageHandler;
import de.mazdermind.gintercom.shared.controlserver.messages.ohai.Capabilities;
import de.mazdermind.gintercom.shared.controlserver.messages.ohai.OhaiMessage;

@Component
@Lazy
public class ControlServerSessionHandler implements StompSessionHandler {
	private static Logger log = LoggerFactory.getLogger(ControlServerSessionHandler.class);
	private final ProvisionMessageHandler provisionMessageHandler;

	public ControlServerSessionHandler(
		@Autowired ProvisionMessageHandler provisionMessageHandler
	) {
		this.provisionMessageHandler = provisionMessageHandler;
		log.info("Created");
	}

	@Override
	public void afterConnected(@NonNull StompSession stompSession, @NonNull StompHeaders stompHeaders) {
		stompSession.subscribe("/provision", provisionMessageHandler);

		stompSession.send("/ohai", new OhaiMessage()
			.setClientId("foo")
			.setClientModel("bar")
			.setProtocolVersion(1)
			.setCapabilities(new Capabilities()
				.setButtons(ImmutableList.of("l", "m", "r"))));
	}


	@Override
	public void handleException(@NonNull StompSession stompSession, StompCommand stompCommand, @NonNull StompHeaders stompHeaders, @NonNull byte[] bytes, @NonNull Throwable throwable) {
		log.info("handleException {}", throwable.getMessage());
	}

	@Override
	public void handleTransportError(@NonNull StompSession stompSession, @NonNull Throwable throwable) {
		log.info("handleTransportError {}", throwable.getMessage());
	}

	@Override
	@NonNull
	public Type getPayloadType(@NonNull StompHeaders stompHeaders) {
		return Object.class;
	}

	@Override
	public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {
		log.info("handleFrame");
	}

}
