package de.mazdermind.gintercom.matrix.provisioning;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.clientapi.configuration.ButtonAction;
import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.ButtonDirection;
import de.mazdermind.gintercom.clientapi.configuration.ButtonTargetType;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociationRequestMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociationRequestMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeProvisionMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ProvisionMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.TimeoutManager;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.PanelGroupsChangedEvent;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProvisioningManagerIT extends ControlServerTestBase {
	private static final String PANEL_ID = "THE_PANEL_ID";
	private static final String RX_GROUP = "THE_RX_GROUP";
	private static final String TX_GROUP_1 = "THE_TX_GROUP_1";
	private static final String TX_GROUP_2 = "THE_TX_GROUP_2";

	private PanelConfig panelConfig;

	@Autowired
	private TimeoutManager timeoutManager;

	@Autowired
	private TestConfig testConfig;

	@Autowired
	private MixingCore mixingCore;

	@Before
	public void prepareConfig() {
		panelConfig = new PanelConfig()
			.setClientId(HOST_ID)
			.setDisplay("THE_DISPLAY_NAME")
			.setRxGroups(ImmutableSet.of(RX_GROUP))
			.setTxGroups(ImmutableSet.of(TX_GROUP_1, TX_GROUP_2))
			.setButtons(ImmutableMap.of(
				"Q1", new ButtonConfig()
					.setDisplay("Phone Home")
					.setAction(ButtonAction.PUSH)
					.setDirection(ButtonDirection.RX)
					.setTarget("Home")
					.setTargetType(ButtonTargetType.GROUP)
			));

		mixingCore.addGroup(RX_GROUP);
		mixingCore.addGroup(TX_GROUP_1);
		mixingCore.addGroup(TX_GROUP_2);
	}

	@Test
	public void doesProvisionKnownClientsOnAssociation() {
		// Configure
		testConfig.getPanels().put(PANEL_ID, panelConfig);

		// Request
		client.transmit(new AssociationRequestMessage()
			.setClientId(HOST_ID)
			.setCapabilities(new AssociationRequestMessage.Capabilities()
				.setButtons(ImmutableList.of("Q1"))));

		// Matrix-Events
		eventReceiver.awaitEvent(ClientAssociatedEvent.class);

		PanelGroupsChangedEvent panelGroupsChangedEvent = eventReceiver.awaitEvent(PanelGroupsChangedEvent.class);
		assertThat(panelGroupsChangedEvent.getAssociation().getClientId()).isEqualTo(HOST_ID);
		assertThat(panelGroupsChangedEvent.getRxGroups()).containsOnly(RX_GROUP);
		assertThat(panelGroupsChangedEvent.getTxGroups()).containsOnly(TX_GROUP_1, TX_GROUP_2);

		// Message-Broadcast
		eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);

		// Response
		client.awaitMessage(AssociatedMessage.class);

		ProvisionMessage provisionMessage = client.awaitMessage(ProvisionMessage.class);
		assertThat(provisionMessage.getDisplay()).isEqualTo(panelConfig.getDisplay());
		assertThat(provisionMessage.getButtons()).isEqualTo(panelConfig.getButtons());
	}

	@Test
	public void doesDeProvisionKnownClientsOnDeAssociation() {
		associateAndProvisionClient();

		// Request
		client.transmit(new DeAssociationRequestMessage().setReason("Test"));

		// Message-Broadcast
		eventReceiver.awaitEvent(DeAssociationRequestMessage.ClientMessage.class);

		// Matrix-Events
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
		ClientDeAssociatedEvent clientDeAssociatedEvent = eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);
		assertThat(clientDeAssociatedEvent.getReason()).contains("HeartBeat Timeout");

		// Response
		DeProvisionMessage deProvisionMessage = client.awaitMessage(DeProvisionMessage.class);
		assertThat(deProvisionMessage).isNotNull();

		DeAssociatedMessage deAssociatedMessage = client.awaitMessage(DeAssociatedMessage.class);
		assertThat(deAssociatedMessage.getReason()).contains("HeartBeat Timeout");
	}

	@Test
	public void provisionsInitialGroupsAndButtonsOnAssociation() {
		panelConfig
			.setButtons(ImmutableMap.of(
				"Q1", new ButtonConfig()
					.setDisplay("Phone Home")
					.setAction(ButtonAction.PUSH)
					.setTarget("Home")
					.setTargetType(ButtonTargetType.GROUP),
				"Q2", new ButtonConfig()
					.setDisplay("Phone Home")
					.setAction(ButtonAction.TOGGLE)
					.setTarget("Home")
					.setTargetType(ButtonTargetType.GROUP)
			));
	}

	protected void associateAndProvisionClient() {
		testConfig.getPanels().put(PANEL_ID, panelConfig);

		client.transmit(new AssociationRequestMessage().setClientId(HOST_ID));

		eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.awaitEvent(PanelGroupsChangedEvent.class);
		eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);

		client.awaitMessage(AssociatedMessage.class);
		client.awaitMessage(ProvisionMessage.class);
	}

	private void setLastTimeoutIntoPast() {
		associatedClientsManager.getAssociation(HOST_ID).setLastHeartbeat(
			LocalDateTime.now().minus(Duration.ofMinutes(30)));
	}
}
