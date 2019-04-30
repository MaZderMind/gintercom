package de.mazdermind.gintercom.shared.controlserver;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import de.mazdermind.gintercom.shared.controlserver.model.provision.ProvisionMessage;

class ProvisionMessageHandler implements StompFrameHandler {
	private static Logger log = LoggerFactory.getLogger(ProvisionMessageHandler.class);

	@Override
	@NonNull
	public Type getPayloadType(@NonNull StompHeaders stompHeaders) {
		return ProvisionMessage.class;
	}

	@Override
	public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {
		ProvisionMessage message = (ProvisionMessage) o;
		log.info("Received ProvisionMessage with Display-Name {}", message.getDisplay());
	}
}
