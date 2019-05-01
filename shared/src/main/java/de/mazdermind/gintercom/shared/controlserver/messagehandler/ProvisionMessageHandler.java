package de.mazdermind.gintercom.shared.controlserver.messagehandler;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;

@Component
@Lazy
public
class ProvisionMessageHandler implements MatrixMessageHandler {
	private static Logger log = LoggerFactory.getLogger(ProvisionMessageHandler.class);
	private final ApplicationEventPublisher eventPublisher;

	public ProvisionMessageHandler(
		@Autowired ApplicationEventPublisher eventPublisher
		) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	@NonNull
	public Type getPayloadType(@NonNull StompHeaders stompHeaders) {
		return ProvisionMessage.class;
	}

	@Override
	public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {
		ProvisionMessage provisionMessage = (ProvisionMessage) o;
		log.info("Received ProvisionMessage with Display-Name {}", provisionMessage.getDisplay());

		eventPublisher.publishEvent(new DoProvisionEvent(provisionMessage));
	}

	@Override
	public String getDestination() {
		return "/provision";
	}
}
