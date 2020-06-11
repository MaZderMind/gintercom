package de.mazdermind.gintercom.matrix.controlserver;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.HeartbeatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeoutManager {
	private final AssociatedClientsManager associatedClientsManager;

	@Scheduled(fixedRateString = "PT30S")
	public void deAssociateTimedOutClients() {
		associatedClientsManager.getAssociations().stream()
			.filter(ClientAssociation::isTimedOut)
			.forEach(this::handleTimedOutClient);
	}

	@EventListener
	public void handleHeartBeat(HeartbeatMessage.ClientMessage message) {
		associatedClientsManager.getAssociation(message.getHostId())
			.registerHeartbeat();
	}

	private void handleTimedOutClient(ClientAssociation clientAssociation) {
		log.info("DeAssociating Host-ID {} because of HeartBeat Timeout (Last Heartbeat received at {})",
			clientAssociation.getHostId(), clientAssociation.getLastHeartbeat());

		String reason = String.format("HeartBeat Timeout (Last Heartbeat received at %s)", clientAssociation.getLastHeartbeat());

		associatedClientsManager.deAssociate(clientAssociation, reason);
	}
}
