package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

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
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AssociationIT extends ControlServerTestBase {
	@Test
	public void canAssociateClient() {
		// Request
		client.transmit(new AssociationRequestMessage()
			.setClientId(HOST_ID)
			.setCapabilities(new AssociationRequestMessage.Capabilities()
				.setButtons(ImmutableList.of("Q1"))));

		// Matrix-Event
		ClientAssociatedEvent clientAssociatedEvent = eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		assertThat(clientAssociatedEvent.getAssociation().getClientId()).isEqualTo(HOST_ID);

		// Message-Broadcast
		AssociationRequestMessage.ClientMessage associateMessage = eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);
		assertThat(associateMessage.getClientId()).isEqualTo(HOST_ID);
		assertThat(associateMessage.getMessage().getClientId()).isEqualTo(HOST_ID);
		assertThat(associateMessage.getMessage().getCapabilities().getButtons()).contains("Q1");

		// Response
		AssociatedMessage associatedMessage = client.awaitMessage(AssociatedMessage.class);
		assertThat(associatedMessage.getRtpMatrixToPanelPort()).isNotNegative().isNotZero();
		assertThat(associatedMessage.getRtpPanelToMatrixPort()).isNotNegative().isNotZero();
	}

	@Test
	public void canAssociateTwoClients() {
		TestControlClient client2 = beanFactory.getBean(TestControlClient.class);
		client2.bind();

		// Request 1
		client.transmit(new AssociationRequestMessage().setClientId(HOST_ID_1));

		// Matrix-Event 1
		ClientAssociatedEvent clientAssociatedEvent1 = eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		assertThat(clientAssociatedEvent1.getAssociation().getClientId()).isEqualTo(HOST_ID_1);

		// Message-Broadcast 1
		AssociationRequestMessage.ClientMessage associateMessage1 = eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);
		assertThat(associateMessage1.getClientId()).isEqualTo(HOST_ID_1);
		assertThat(associateMessage1.getMessage().getClientId()).isEqualTo(HOST_ID);

		// Response 1
		AssociatedMessage associatedMessage1 = client.awaitMessage(AssociatedMessage.class);
		assertThat(associatedMessage1.getRtpMatrixToPanelPort()).isNotNegative().isNotZero();
		assertThat(associatedMessage1.getRtpPanelToMatrixPort()).isNotNegative().isNotZero();


		// Request 2
		client2.transmit(new AssociationRequestMessage().setClientId(HOST_ID_2));

		// Matrix-Event 2
		ClientAssociatedEvent clientAssociatedEvent2 = eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		assertThat(clientAssociatedEvent2.getAssociation().getClientId()).isEqualTo(HOST_ID_2);

		// Message-Broadcast 2
		AssociationRequestMessage.ClientMessage associateMessage2 = eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);
		assertThat(associateMessage2.getClientId()).isEqualTo(HOST_ID_2);
		assertThat(associateMessage2.getMessage().getClientId()).isEqualTo(HOST_ID_2);

		// Response 2
		AssociatedMessage associatedMessage2 = client2.awaitMessage(AssociatedMessage.class);
		assertThat(associatedMessage2.getRtpMatrixToPanelPort()).isNotNegative().isNotZero();
		assertThat(associatedMessage2.getRtpPanelToMatrixPort()).isNotNegative().isNotZero();

		// Comparison
		assertThat(associatedMessage1.getRtpMatrixToPanelPort())
			.isNotEqualTo(associatedMessage2.getRtpMatrixToPanelPort());

		assertThat(associatedMessage1.getRtpPanelToMatrixPort())
			.isNotEqualTo(associatedMessage2.getRtpPanelToMatrixPort());

		// Shutdown
		client2.shutdown();
		client2.assertNoMoreMessages();
	}

	@Test
	public void cantAssociateClientTwice() {
		client.transmit(new AssociationRequestMessage().setClientId(HOST_ID));

		client.awaitMessage(AssociatedMessage.class);

		eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);

		client.transmit(new AssociationRequestMessage().setClientId(HOST_ID));

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains(HOST_ID)
			.contains("is already associated");
	}

	@Test
	public void canDeAssociateClient() {
		final String REASON = "Test";

		client.transmit(new AssociationRequestMessage().setClientId(HOST_ID));

		client.awaitMessage(AssociatedMessage.class);
		eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);

		client.transmit(new DeAssociationRequestMessage()
			.setReason(REASON));

		DeAssociatedMessage deAssociatedMessage = client.awaitMessage(DeAssociatedMessage.class);
		assertThat(deAssociatedMessage.getReason())
			.contains("Received DeAssociationRequestMessage")
			.contains(REASON);

		DeAssociationRequestMessage.ClientMessage deAssociateMessage = eventReceiver.awaitEvent(DeAssociationRequestMessage.ClientMessage.class);
		assertThat(deAssociateMessage.getClientId()).isEqualTo(HOST_ID);
		assertThat(deAssociateMessage.getMessage().getReason()).isEqualTo(REASON);

		ClientDeAssociatedEvent clientDeAssociatedEvent = eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);
		assertThat(clientDeAssociatedEvent.getAssociation().getClientId()).isEqualTo(HOST_ID);
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
