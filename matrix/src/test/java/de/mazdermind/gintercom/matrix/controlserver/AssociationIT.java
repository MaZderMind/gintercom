package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociationRequestMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociationRequestMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ErrorMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AssociationIT extends ControlServerTestBase {
	private String clientId1;
	private String clientId2;

	@Before
	public void before() {
		clientId1 = TestClientIdGenerator.generateTestClientId();
		clientId2 = TestClientIdGenerator.generateTestClientId();
	}

	@Test
	public void canAssociateClient() {
		// Request
		client.transmit(new AssociationRequestMessage()
			.setClientId(clientId1)
			.setCapabilities(new AssociationRequestMessage.Capabilities()
				.setButtons(ImmutableList.of("Q1"))));

		// Matrix-Event
		ClientAssociatedEvent clientAssociatedEvent = eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		assertThat(clientAssociatedEvent.getAssociation().getClientId()).isEqualTo(clientId1);

		// Message-Broadcast
		AssociationRequestMessage.ClientMessage associateMessage = eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);
		assertThat(associateMessage.getClientId()).isEqualTo(clientId1);
		assertThat(associateMessage.getMessage().getClientId()).isEqualTo(clientId1);
		assertThat(associateMessage.getMessage().getCapabilities().getButtons()).contains("Q1");

		// Response
		AssociatedMessage associatedMessage = client.awaitMessage(AssociatedMessage.class);
		assertThat(associatedMessage.getRtpMatrixToClientPort()).isNotNegative().isNotZero();
		assertThat(associatedMessage.getRtpClientToMatrixPort()).isNotNegative().isNotZero();
	}

	@Test
	public void canAssociateTwoClients() {
		TestControlClient client2 = beanFactory.getBean(TestControlClient.class);
		client2.bind();

		// Request 1
		client.transmit(new AssociationRequestMessage().setClientId(clientId1));

		// Matrix-Event 1
		ClientAssociatedEvent clientAssociatedEvent1 = eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		assertThat(clientAssociatedEvent1.getAssociation().getClientId()).isEqualTo(clientId1);

		// Message-Broadcast 1
		AssociationRequestMessage.ClientMessage associateMessage1 = eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);
		assertThat(associateMessage1.getClientId()).isEqualTo(clientId1);
		assertThat(associateMessage1.getMessage().getClientId()).isEqualTo(clientId1);

		// Response 1
		AssociatedMessage associatedMessage1 = client.awaitMessage(AssociatedMessage.class);
		assertThat(associatedMessage1.getRtpMatrixToClientPort()).isNotNegative().isNotZero();
		assertThat(associatedMessage1.getRtpClientToMatrixPort()).isNotNegative().isNotZero();


		// Request 2
		client2.transmit(new AssociationRequestMessage().setClientId(clientId2));

		// Matrix-Event 2
		ClientAssociatedEvent clientAssociatedEvent2 = eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		assertThat(clientAssociatedEvent2.getAssociation().getClientId()).isEqualTo(clientId2);

		// Message-Broadcast 2
		AssociationRequestMessage.ClientMessage associateMessage2 = eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);
		assertThat(associateMessage2.getClientId()).isEqualTo(clientId2);
		assertThat(associateMessage2.getMessage().getClientId()).isEqualTo(clientId2);

		// Response 2
		AssociatedMessage associatedMessage2 = client2.awaitMessage(AssociatedMessage.class);
		assertThat(associatedMessage2.getRtpMatrixToClientPort()).isNotNegative().isNotZero();
		assertThat(associatedMessage2.getRtpClientToMatrixPort()).isNotNegative().isNotZero();

		// Comparison
		assertThat(associatedMessage1.getRtpMatrixToClientPort())
			.isNotEqualTo(associatedMessage2.getRtpMatrixToClientPort());

		assertThat(associatedMessage1.getRtpClientToMatrixPort())
			.isNotEqualTo(associatedMessage2.getRtpClientToMatrixPort());

		// Shutdown
		client2.shutdown();
		client2.assertNoMoreMessages();
	}

	@Test
	public void cantAssociateClientTwice() {
		client.transmit(new AssociationRequestMessage().setClientId(clientId1));

		client.awaitMessage(AssociatedMessage.class);

		eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);

		client.transmit(new AssociationRequestMessage().setClientId(clientId1));

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains(clientId1)
			.contains("is already associated");
	}

	@Test
	public void canDeAssociateClient() {
		final String REASON = "Test";

		client.transmit(new AssociationRequestMessage().setClientId(clientId1));

		client.awaitMessage(AssociatedMessage.class);
		eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);

		client.transmit(new DeAssociationRequestMessage()
			.setReason(REASON));

		DeAssociatedMessage deAssociatedMessage = client.awaitMessage(DeAssociatedMessage.class);
		assertThat(deAssociatedMessage.getReason())
			.contains("Received DeAssociationRequestMessage")
			.contains(REASON);

		ClientDeAssociatedEvent clientDeAssociatedEvent = eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);
		assertThat(clientDeAssociatedEvent.getAssociation().getClientId()).isEqualTo(clientId1);

		DeAssociationRequestMessage.ClientMessage deAssociateMessage = eventReceiver
			.awaitEvent(DeAssociationRequestMessage.ClientMessage.class);
		assertThat(deAssociateMessage.getClientId()).isEqualTo(clientId1);
		assertThat(deAssociateMessage.getMessage().getReason()).isEqualTo(REASON);
	}

	@Test
	public void cantDeAssociateUnAssociatedClient() {
		client.transmit(new DeAssociationRequestMessage()
			.setReason("Test"));

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains("is not not associated");
	}
}
