package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.ClientHeartbeatMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.MatrixHeartbeatMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;

public class ControlServerIT extends ControlServerTestBase {
	@Autowired
	private MessageSender messageSender;

	@Before
	public void before() {
		associateClient();
	}

	@Test
	public void clientToMatrixMessage() {
		client.transmit(new ClientHeartbeatMessage());
		ClientHeartbeatMessage.ClientMessage receivedEvent = eventReceiver.awaitEvent(ClientHeartbeatMessage.ClientMessage.class);

		assertThat(receivedEvent.getClientId()).isEqualTo(HOST_ID);
	}

	@Test
	public void matrixToClientMessage() {
		messageSender.sendMessageTo(HOST_ID, new MatrixHeartbeatMessage());

		client.awaitMessage(MatrixHeartbeatMessage.class);
	}
}
