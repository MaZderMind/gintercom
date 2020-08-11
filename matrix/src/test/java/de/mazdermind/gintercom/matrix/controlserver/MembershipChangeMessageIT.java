package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.clientapi.configuration.CommunicationTargetType;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.MembershipChangeMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;
import de.mazdermind.gintercom.mixingcore.Client;
import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MembershipChangeMessageIT extends ControlServerTestBase {
	@Autowired
	private MixingCore mixingCore;

	@Autowired
	private TestConfig testConfig;

	private String groupId;
	private String clientId;

	@Before
	public void before() {
		clientId = TestClientIdGenerator.generateTestClientId();
		testConfig.addRandomPanel(clientId);
		groupId = testConfig.addRandomGroup();

		associateClient(clientId);
		mixingCore.addGroup(groupId);
	}

	@Test
	public void canJoinTxGroup() {
		assertThat(mixingCore.getClientById(clientId).getTxGroups()).isEmpty();

		client.transmit(new MembershipChangeMessage()
			.setChange(MembershipChangeMessage.Change.JOIN)
			.setTargetType(CommunicationTargetType.GROUP)
			.setTarget(groupId));

		eventReceiver.awaitEvent(MembershipChangeMessage.ClientMessage.class);
		assertThat(mixingCore.getClientById(clientId).getTxGroups())
			.extracting(Group::getId)
			.containsExactly(groupId);
	}

	@Test
	public void canLeaveTxGroup() {
		Client client = mixingCore.getClientById(clientId);
		client.startTransmittingTo(mixingCore.getGroupById(groupId));

		assertThat(client.getTxGroups())
			.extracting(Group::getId)
			.containsExactly(groupId);

		this.client.transmit(new MembershipChangeMessage()
			.setChange(MembershipChangeMessage.Change.LEAVE)
			.setTargetType(CommunicationTargetType.GROUP)
			.setTarget(groupId));

		eventReceiver.awaitEvent(MembershipChangeMessage.ClientMessage.class);
		assertThat(client.getTxGroups()).isEmpty();
	}

	@Test
	@Ignore("Feature not yet Implemented")
	public void canStartTxToPanel() {

	}

	@Test
	@Ignore("Feature not yet Implemented")
	public void canStopTxPanel() {

	}
}
