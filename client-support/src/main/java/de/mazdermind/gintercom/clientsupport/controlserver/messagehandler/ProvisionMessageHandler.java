package de.mazdermind.gintercom.clientsupport.controlserver.messagehandler;

import java.lang.reflect.Type;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.clientsupport.controlserver.ConnectionLifecycleManager;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.clientsupport.controlserver.events.provision.ProvisionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProvisionMessageHandler implements MatrixMessageHandler {
	private final ApplicationEventPublisher eventPublisher;
	private final ConnectionLifecycleManager connectionLifecycleManager;

	@Override
	@NonNull
	public Type getPayloadType(@NonNull StompHeaders stompHeaders) {
		return ProvisionMessage.class;
	}

	@Override
	public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {
		ProvisionMessage provisionMessage = (ProvisionMessage) o;
		log.info("Received ProvisionMessage with Display-Name {}", provisionMessage.getProvisioningInformation().getDisplay());

		Optional<MatrixAddressDiscoveryServiceResult> discoveredMatrix = connectionLifecycleManager.getDiscoveredMatrix();
		if (!discoveredMatrix.isPresent()) {
			log.error("Not Connected to Matrix anymore");
			return;
		}

		eventPublisher.publishEvent(new ProvisionEvent(
			provisionMessage.getProvisioningInformation(),
			discoveredMatrix.get()
		));
	}

	@Override
	public String getDestination() {
		return "/user/provision";
	}
}
