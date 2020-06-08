package de.mazdermind.gintercom.matrix.controlserver.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.HeartbeatMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import de.mazdermind.gintercom.matrix.controlserver.TimeoutManager;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatTimeoutIT extends ControlServerTestBase {
	@Autowired
	private AssociatedClientsManager associatedClientsManager;

	@Autowired
	private TimeoutManager timeoutManager;

	@Before
	public void doAssociateClient() {
		associateClient();
	}

	@Test
	public void keepsNotTimedOutClients() {
		timeoutManager.deAssociateTimedOutClients();

		Optional<ClientAssociation> association = associatedClientsManager.findAssociation(HOST_ID);
		assertThat(association).isPresent();
	}

	@Test
	public void deAssociatesTimedOutClients() {
		setLastTimeoutIntoPast();

		timeoutManager.deAssociateTimedOutClients();

		Optional<ClientAssociation> association = associatedClientsManager.findAssociation(HOST_ID);
		assertThat(association).isEmpty();

		DeAssociatedMessage deAssociateMessage = client.awaitMessage(DeAssociatedMessage.class);
		assertThat(deAssociateMessage.getReason())
			.contains("HeartBeat Timeout");

		ClientDeAssociatedEvent deAssociatedEvent = eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);
		assertThat(deAssociatedEvent.getAssociation().getHostId()).isEqualTo(HOST_ID);
	}

	@Test
	public void registersHeartbeatMessages() {
		setLastTimeoutIntoPast();

		Optional<ClientAssociation> association1 = associatedClientsManager.findAssociation(HOST_ID);
		assertThat(association1).isPresent();
		assertThat(association1.get().isTimedOut()).isTrue();

		sendHeartbeat();

		Optional<ClientAssociation> association2 = associatedClientsManager.findAssociation(HOST_ID);
		assertThat(association2).isPresent();
		assertThat(association2.get().isTimedOut()).isFalse();
	}

	private void sendHeartbeat() {
		timeoutManager.handleHeartBeat((HeartbeatMessage.ClientMessage) new HeartbeatMessage.ClientMessage()
			.setHostId(HOST_ID)
			.setMessage(new HeartbeatMessage()));
	}

	private void setLastTimeoutIntoPast() {
		associatedClientsManager.getAssociation(HOST_ID).setLastHeartbeat(
			LocalDateTime.now().minus(Duration.ofMinutes(30)));
	}
}
