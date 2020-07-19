package de.mazdermind.gintercom.clientsupport.controlserver;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.ClientHeartbeatMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.MatrixHeartbeatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@RequiredArgsConstructor
public class ClientTimeoutManager {
	private static final String HEARTBEAT_VALIDATION_GAP = "PT10S";
	private static final Duration HEARTBEAT_TIMEOUT = Duration.ofSeconds(15);

	private final ClientMessageSender messageSender;
	private final ClientAssociationManager associationManager;

	private LocalDateTime lastHeartbeat = null;

	@EventListener(MatrixHeartbeatMessage.class)
	public void handleMatrixHeartbeatMessage() {
		lastHeartbeat = LocalDateTime.now();
		messageSender.sendMessage(new ClientHeartbeatMessage());
	}


	@Scheduled(fixedRateString = HEARTBEAT_VALIDATION_GAP)
	public void checkMatrixTimeout() {
		if (lastHeartbeat != null && isTimedOut()) {
			String reason = String.format("HeartBeat Timeout (Last Heartbeat received at %s)", lastHeartbeat);
			log.info(reason);
			associationManager.deAssociate(reason);
			lastHeartbeat = null;
		}
	}

	public boolean isTimedOut() {
		return lastHeartbeat.plus(HEARTBEAT_TIMEOUT).isBefore(LocalDateTime.now());
	}
}
