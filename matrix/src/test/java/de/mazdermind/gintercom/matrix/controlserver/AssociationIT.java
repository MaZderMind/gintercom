package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.Capabilities;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociateMessage;
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
		client.transmit(new AssociateMessage()
			.setHostId(HOST_ID)
			.setCapabilities(new Capabilities()
				.setButtons(ImmutableList.of("Q1"))));

		// Matrix-Event
		ClientAssociatedEvent clientAssociatedEvent = eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		assertThat(clientAssociatedEvent.getAssociation().getHostId()).isEqualTo(HOST_ID);

		// Message-Broadcast
		AssociateMessage.ClientMessage associateMessage = eventReceiver.awaitEvent(AssociateMessage.ClientMessage.class);
		assertThat(associateMessage.getHostId()).isEqualTo(HOST_ID);
		assertThat(associateMessage.getMessage().getHostId()).isEqualTo(HOST_ID);
		assertThat(associateMessage.getMessage().getCapabilities().getButtons()).contains("Q1");

		// Response
		AssociatedMessage associatedMessage = client.awaitMessage(AssociatedMessage.class);
		assertThat(associatedMessage.getRtpMatrixToPanelPort()).isNotNegative().isNotZero();
		assertThat(associatedMessage.getRtpPanelToMatrixPort()).isNotNegative().isNotZero();
	}

	@Test
	public void canAssociateTwoHostIds() {
		TestControlClient client2 = beanFactory.getBean(TestControlClient.class);
		client2.bind();

		// Request 1
		client.transmit(new AssociateMessage().setHostId(HOST_ID_1));

		// Matrix-Event 1
		ClientAssociatedEvent clientAssociatedEvent1 = eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		assertThat(clientAssociatedEvent1.getAssociation().getHostId()).isEqualTo(HOST_ID_1);

		// Message-Broadcast 1
		AssociateMessage.ClientMessage associateMessage1 = eventReceiver.awaitEvent(AssociateMessage.ClientMessage.class);
		assertThat(associateMessage1.getHostId()).isEqualTo(HOST_ID_1);
		assertThat(associateMessage1.getMessage().getHostId()).isEqualTo(HOST_ID);

		// Response 1
		AssociatedMessage associatedMessage1 = client.awaitMessage(AssociatedMessage.class);
		assertThat(associatedMessage1.getRtpMatrixToPanelPort()).isNotNegative().isNotZero();
		assertThat(associatedMessage1.getRtpPanelToMatrixPort()).isNotNegative().isNotZero();


		// Request 2
		client2.transmit(new AssociateMessage().setHostId(HOST_ID_2));

		// Matrix-Event 2
		ClientAssociatedEvent clientAssociatedEvent2 = eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		assertThat(clientAssociatedEvent2.getAssociation().getHostId()).isEqualTo(HOST_ID_2);

		// Message-Broadcast 2
		AssociateMessage.ClientMessage associateMessage2 = eventReceiver.awaitEvent(AssociateMessage.ClientMessage.class);
		assertThat(associateMessage2.getHostId()).isEqualTo(HOST_ID_2);
		assertThat(associateMessage2.getMessage().getHostId()).isEqualTo(HOST_ID_2);

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
		client.transmit(new AssociateMessage().setHostId(HOST_ID));

		client.awaitMessage(AssociatedMessage.class);

		eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.awaitEvent(AssociateMessage.ClientMessage.class);

		client.transmit(new AssociateMessage().setHostId(HOST_ID));

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains(HOST_ID)
			.contains("is already associated");
	}

	@Test
	public void canDeAssociateClient() {
		final String REASON = "Test";

		client.transmit(new AssociateMessage().setHostId(HOST_ID));

		client.awaitMessage(AssociatedMessage.class);
		eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.awaitEvent(AssociateMessage.ClientMessage.class);

		client.transmit(new DeAssociateMessage()
			.setReason(REASON));

		DeAssociatedMessage deAssociatedMessage = client.awaitMessage(DeAssociatedMessage.class);
		assertThat(deAssociatedMessage.getReason())
			.contains("Received DeAssociateMessage")
			.contains(REASON);

		DeAssociateMessage.ClientMessage deAssociateMessage = eventReceiver.awaitEvent(DeAssociateMessage.ClientMessage.class);
		assertThat(deAssociateMessage.getHostId()).isEqualTo(HOST_ID);
		assertThat(deAssociateMessage.getMessage().getReason()).isEqualTo(REASON);

		ClientDeAssociatedEvent clientDeAssociatedEvent = eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);
		assertThat(clientDeAssociatedEvent.getAssociation().getHostId()).isEqualTo(HOST_ID);
	}

	@Test
	public void cantDeAssociateUnAssociatedClient() {
		client.transmit(new DeAssociateMessage()
			.setReason("Test"));

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains("is not not associated");
	}
}
