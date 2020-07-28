package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.ClientHeartbeatMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.MatrixHeartbeatMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatTimeoutIT extends ControlServerTestBase {
	@Autowired
	private TimeoutManager timeoutManager;
	private String clientId;

	@Before
	public void doAssociateClient() {
		clientId = TestClientIdGenerator.generateTestClientId();
		associateClient(clientId);
	}

	@Test
	public void keepsNotTimedOutClients() {
		timeoutManager.deAssociateTimedOutClients();

		Optional<ClientAssociation> association = associatedClientsManager.findAssociation(clientId);
		assertThat(association).isPresent();
	}

	@Test
	public void deAssociatesTimedOutClients() {
		setLastTimeoutIntoPast();

		timeoutManager.deAssociateTimedOutClients();

		Optional<ClientAssociation> association = associatedClientsManager.findAssociation(clientId);
		assertThat(association).isEmpty();

		DeAssociatedMessage deAssociateMessage = client.awaitMessage(DeAssociatedMessage.class);
		assertThat(deAssociateMessage.getReason()).matches("HeartBeat Timeout \\(Last Heartbeat received at .*\\)");

		ClientDeAssociatedEvent deAssociatedEvent = eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);
		assertThat(deAssociatedEvent.getAssociation().getClientId()).isEqualTo(clientId);
		assertThat(deAssociatedEvent.getReason()).matches("HeartBeat Timeout \\(Last Heartbeat received at .*\\)");
	}

	@Test
	public void registersHeartbeatMessages() {
		setLastTimeoutIntoPast();

		Optional<ClientAssociation> association1 = associatedClientsManager.findAssociation(clientId);
		assertThat(association1).isPresent();
		assertThat(association1.get().isTimedOut()).isTrue();

		sendHeartbeat();

		Optional<ClientAssociation> association2 = associatedClientsManager.findAssociation(clientId);
		assertThat(association2).isPresent();
		assertThat(association2.get().isTimedOut()).isFalse();

		client.maybeAwaitMessage(MatrixHeartbeatMessage.class);
	}

	private void sendHeartbeat() {
		timeoutManager.handleHeartBeat((ClientHeartbeatMessage.ClientMessage) new ClientHeartbeatMessage.ClientMessage()
			.setClientId(clientId)
			.setMessage(new ClientHeartbeatMessage()));
	}

	private void setLastTimeoutIntoPast() {
		associatedClientsManager.getAssociation(clientId).setLastHeartbeat(
			LocalDateTime.now().minus(Duration.ofMinutes(30)));
	}
}
