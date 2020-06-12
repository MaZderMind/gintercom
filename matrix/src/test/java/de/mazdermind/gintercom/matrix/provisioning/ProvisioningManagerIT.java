package de.mazdermind.gintercom.matrix.provisioning;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.mazdermind.gintercom.clientapi.configuration.ButtonAction;
import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.ButtonTargetType;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeProvisionMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ProvisionMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.TimeoutManager;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.PanelAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.PanelDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProvisioningManagerIT extends ControlServerTestBase {
	private static final PanelConfig PANEL_CONFIG = new PanelConfig()
		.setHostId(HOST_ID)
		.setDisplay("THE_DISPLAY_NAME")
		.setButtons(ImmutableMap.of(
			"Q1", new ButtonConfig()
				.setDisplay("Phone Home")
				.setAction(ButtonAction.PTT)
				.setTarget("Home")
				.setTargetType(ButtonTargetType.GROUP)
		));
	private static final String PANEL_ID = "THE_PANEL_ID";
	@Autowired
	private TimeoutManager timeoutManager;
	@Autowired
	private TestConfig testConfig;

	@Test
	public void doesProvisionKnownClientsOnAssociation() {
		// Configure
		testConfig.getPanels().put(PANEL_ID, PANEL_CONFIG);

		// Request
		client.transmit(new AssociateMessage()
			.setHostId(HOST_ID)
			.setCapabilities(new AssociateMessage.Capabilities()
				.setButtons(ImmutableList.of("Q1"))));

		// Matrix-Events
		eventReceiver.awaitEvent(ClientAssociatedEvent.class);

		PanelAssociatedEvent panelAssociatedEvent = eventReceiver.awaitEvent(PanelAssociatedEvent.class);
		assertThat(panelAssociatedEvent.getAssociation().getHostId()).isEqualTo(HOST_ID);
		assertThat(panelAssociatedEvent.getPanelId()).isEqualTo(PANEL_ID);
		assertThat(panelAssociatedEvent.getPanelConfig()).isEqualTo(PANEL_CONFIG);

		// Message-Broadcast
		eventReceiver.awaitEvent(AssociateMessage.ClientMessage.class);

		// Response
		client.awaitMessage(AssociatedMessage.class);

		ProvisionMessage provisionMessage = client.awaitMessage(ProvisionMessage.class);
		assertThat(provisionMessage.getDisplay()).isEqualTo(PANEL_CONFIG.getDisplay());
		assertThat(provisionMessage.getButtons()).isEqualTo(PANEL_CONFIG.getButtons());
	}

	@Test
	public void doesDeProvisionKnownClientsOnDeAssociation() {
		associateAndProvisionClient();

		// Request
		client.transmit(new DeAssociateMessage().setReason("Test"));

		// Message-Broadcast
		eventReceiver.awaitEvent(DeAssociateMessage.ClientMessage.class);

		// Matrix-Events
		PanelDeAssociatedEvent panelDeAssociatedEvent = eventReceiver.awaitEvent(PanelDeAssociatedEvent.class);
		assertThat(panelDeAssociatedEvent.getPanelId()).isEqualTo(PANEL_ID);
		assertThat(panelDeAssociatedEvent.getAssociation().getHostId()).isEqualTo(HOST_ID);

		eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);

		// Response
		DeProvisionMessage deProvisionMessage = client.awaitMessage(DeProvisionMessage.class);
		assertThat(deProvisionMessage).isNotNull();

		client.awaitMessage(DeAssociatedMessage.class);
	}

	@Test
	public void doesDeProvisionClientOnTimeout() {
		associateAndProvisionClient();
		setLastTimeoutIntoPast();

		timeoutManager.deAssociateTimedOutClients();

		// Matrix-Events
		PanelDeAssociatedEvent panelDeAssociatedEvent = eventReceiver.awaitEvent(PanelDeAssociatedEvent.class);
		assertThat(panelDeAssociatedEvent.getPanelId()).isEqualTo(PANEL_ID);
		assertThat(panelDeAssociatedEvent.getAssociation().getHostId()).isEqualTo(HOST_ID);

		ClientDeAssociatedEvent clientDeAssociatedEvent = eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);
		assertThat(clientDeAssociatedEvent.getReason()).contains("HeartBeat Timeout");

		// Response
		DeProvisionMessage deProvisionMessage = client.awaitMessage(DeProvisionMessage.class);
		assertThat(deProvisionMessage).isNotNull();

		DeAssociatedMessage deAssociatedMessage = client.awaitMessage(DeAssociatedMessage.class);
		assertThat(deAssociatedMessage.getReason()).contains("HeartBeat Timeout");
	}

	protected void associateAndProvisionClient() {
		testConfig.getPanels().put(PANEL_ID, PANEL_CONFIG);

		client.transmit(new AssociateMessage().setHostId(HOST_ID));

		eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.awaitEvent(PanelAssociatedEvent.class);
		eventReceiver.awaitEvent(AssociateMessage.ClientMessage.class);

		client.awaitMessage(AssociatedMessage.class);
		client.awaitMessage(ProvisionMessage.class);
	}

	private void setLastTimeoutIntoPast() {
		associatedClientsManager.getAssociation(HOST_ID).setLastHeartbeat(
			LocalDateTime.now().minus(Duration.ofMinutes(30)));
	}
}
