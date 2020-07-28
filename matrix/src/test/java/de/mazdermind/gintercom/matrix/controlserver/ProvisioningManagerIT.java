package de.mazdermind.gintercom.matrix.controlserver;

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
import de.mazdermind.gintercom.clientapi.configuration.CommunicationDirection;
import de.mazdermind.gintercom.clientapi.configuration.CommunicationTargetType;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociationRequestMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociationRequestMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeProvisionMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ProvisionMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.PanelGroupsChangedEvent;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProvisioningManagerIT extends ControlServerTestBase {
	@Autowired
	private TimeoutManager timeoutManager;

	@Autowired
	private TestConfig testConfig;

	@Autowired
	private MixingCore mixingCore;

	private PanelConfig panelConfig;
	private String panelId;
	private String clientId;

	private String rxGroup;
	private String txGroup1;
	private String txGroup2;

	@Before
	public void prepareConfig() {
		rxGroup = testConfig.addRandomGroupToMixingCore();
		txGroup1 = testConfig.addRandomGroupToMixingCore();
		txGroup2 = testConfig.addRandomGroupToMixingCore();

		clientId = TestClientIdGenerator.generateTestClientId();
		panelId = testConfig.addRandomPanel(clientId);
		panelConfig = testConfig.getPanels().get(panelId)
			.setRxGroups(ImmutableSet.of(rxGroup))
			.setTxGroups(ImmutableSet.of(txGroup1, txGroup2))
			.setButtons(ImmutableMap.of(
				"Q1", new ButtonConfig()
					.setDisplay("Phone Home")
					.setAction(ButtonAction.PUSH)
					.setDirection(CommunicationDirection.RX)
					.setTarget("Home")
					.setTargetType(CommunicationTargetType.GROUP)
			));
	}

	@Test
	public void doesProvisionKnownClientsOnAssociation() {
		// Configure
		testConfig.getPanels().put(panelId, panelConfig);

		// Request
		client.transmit(new AssociationRequestMessage()
			.setClientId(clientId)
			.setCapabilities(new AssociationRequestMessage.Capabilities()
				.setButtons(ImmutableList.of("Q1"))));

		// Matrix-Events
		eventReceiver.awaitEvent(ClientAssociatedEvent.class);

		PanelGroupsChangedEvent panelGroupsChangedEvent = eventReceiver.awaitEvent(PanelGroupsChangedEvent.class);
		assertThat(panelGroupsChangedEvent.getAssociation().getClientId()).isEqualTo(clientId);
		assertThat(panelGroupsChangedEvent.getRxGroups()).containsOnly(rxGroup);
		assertThat(panelGroupsChangedEvent.getTxGroups()).containsOnly(txGroup1, txGroup2);

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

		// Matrix-Events
		eventReceiver.awaitEvent(ClientDeAssociatedEvent.class);

		// Message-Broadcast
		eventReceiver.awaitEvent(DeAssociationRequestMessage.ClientMessage.class);

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
					.setTargetType(CommunicationTargetType.GROUP),
				"Q2", new ButtonConfig()
					.setDisplay("Phone Home")
					.setAction(ButtonAction.TOGGLE)
					.setTarget("Home")
					.setTargetType(CommunicationTargetType.GROUP)
			));
	}

	protected void associateAndProvisionClient() {
		client.transmit(new AssociationRequestMessage().setClientId(clientId));

		eventReceiver.awaitEvent(ClientAssociatedEvent.class);
		eventReceiver.awaitEvent(PanelGroupsChangedEvent.class);
		eventReceiver.awaitEvent(AssociationRequestMessage.ClientMessage.class);

		client.awaitMessage(AssociatedMessage.class);
		client.awaitMessage(ProvisionMessage.class);
	}

	private void setLastTimeoutIntoPast() {
		associatedClientsManager.getAssociation(clientId).setLastHeartbeat(
			LocalDateTime.now().minus(Duration.ofMinutes(30)));
	}
}
