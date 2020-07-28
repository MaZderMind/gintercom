package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.ClientHeartbeatMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.MatrixHeartbeatMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;

public class ControlServerIT extends ControlServerTestBase {
	@Autowired
	private MessageSender messageSender;

	private String clientId;

	@Before
	public void before() {
		clientId = TestClientIdGenerator.generateTestClientId();
		associateClient(clientId);
	}

	@Test
	public void clientToMatrixMessage() {
		client.transmit(new ClientHeartbeatMessage());
		ClientHeartbeatMessage.ClientMessage receivedEvent = eventReceiver.awaitEvent(ClientHeartbeatMessage.ClientMessage.class);

		assertThat(receivedEvent.getClientId()).isEqualTo(clientId);
	}

	@Test
	public void matrixToClientMessage() {
		messageSender.sendMessageTo(clientId, new MatrixHeartbeatMessage());

		client.awaitMessage(MatrixHeartbeatMessage.class);
	}
}
