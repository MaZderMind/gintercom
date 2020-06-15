package de.mazdermind.gintercom.clientsupport.controlserver;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.ClientHeartbeatMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.MatrixHeartbeatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@RequiredArgsConstructor
public class HeartbeatResponder {
	private final ClientMessageSender messageSender;

	@EventListener
	public void handleMatrixHeartbeatMessage(MatrixHeartbeatMessage matrixHeartbeatMessage) {
		messageSender.sendMessage(new ClientHeartbeatMessage());
	}
}
