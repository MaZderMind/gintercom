package de.mazdermind.gintercom.matrix.controlserver;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.ClientHeartbeatMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.MatrixHeartbeatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeoutManager {
	static final String HEARTBEAT_VALIDATION_GAP = "PT10S";
	static final String HEARTBEAT_SENDING_GAP = "PT10S";

	private final AssociatedClientsManager associatedClientsManager;
	private final MessageSender messageSender;

	@Scheduled(fixedRateString = HEARTBEAT_VALIDATION_GAP)
	public void deAssociateTimedOutClients() {
		associatedClientsManager.getAssociations().stream()
			.filter(ClientAssociation::isTimedOut)
			.forEach(this::handleTimedOutClient);
	}

	@EventListener
	public void handleHeartBeat(ClientHeartbeatMessage.ClientMessage message) {
		associatedClientsManager.getAssociation(message.getClientId())
			.registerHeartbeat();
	}

	private void handleTimedOutClient(ClientAssociation clientAssociation) {
		log.info("DeAssociating Client-Id {} because of HeartBeat Timeout (Last Heartbeat received at {})",
			clientAssociation.getClientId(), clientAssociation.getLastHeartbeat());

		String reason = String.format("HeartBeat Timeout (Last Heartbeat received at %s)", clientAssociation.getLastHeartbeat());

		associatedClientsManager.deAssociate(clientAssociation, reason);
	}

	@Scheduled(fixedRateString = HEARTBEAT_SENDING_GAP)
	public void sendHeartbeatMessages() {
		associatedClientsManager.getAssociations().forEach(association ->
			messageSender.sendMessageTo(association.getClientId(), new MatrixHeartbeatMessage()));
	}
}
