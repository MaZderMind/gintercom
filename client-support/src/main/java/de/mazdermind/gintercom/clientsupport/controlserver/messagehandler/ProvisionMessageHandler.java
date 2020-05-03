package de.mazdermind.gintercom.clientsupport.controlserver.messagehandler;

import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisionMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProvisionMessageHandler implements MatrixMessageHandler {
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
		log.info("Received ProvisionMessage with Display-Name {}", provisionMessage.getProvisioningInformation().getDisplay());

		eventPublisher.publishEvent(provisionMessage.getProvisioningInformation());
	}

	@Override
	public String getDestination() {
		return "/user/provision";
	}
}
