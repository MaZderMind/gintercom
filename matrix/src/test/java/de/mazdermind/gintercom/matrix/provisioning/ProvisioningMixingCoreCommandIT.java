package de.mazdermind.gintercom.matrix.provisioning;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;
import de.mazdermind.gintercom.mixingcore.Client;
import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;

public class ProvisioningMixingCoreCommandIT extends ControlServerTestBase {

	@Autowired
	private TestConfig testConfig;

	@Autowired
	private MixingCore mixingCore;

	private String clientId;
	private String rxGroup;
	private String txGroup2;
	private String txGroup1;

	@Before
	public void prepareConfig() {
		rxGroup = testConfig.addRandomGroupToMixingCore();
		txGroup2 = testConfig.addRandomGroupToMixingCore();
		txGroup1 = testConfig.addRandomGroupToMixingCore();

		clientId = TestClientIdGenerator.generateTestClientId();
		String panelId = testConfig.addRandomPanel(clientId);
		testConfig.getPanels().get(panelId)
			.setRxGroups(ImmutableSet.of(rxGroup))
			.setTxGroups(ImmutableSet.of(txGroup1, txGroup2));
	}

	@Test
	public void initialGroupsAreLinkedOnProvisioning() {
		associateClient(clientId);

		Client client = mixingCore.getClientById(clientId);
		assertThat(client.getRxGroups())
			.extracting(Group::getId)
			.containsOnly(rxGroup);

		assertThat(client.getTxGroups())
			.extracting(Group::getId)
			.containsOnly(txGroup1, txGroup2);
	}

	@Test
	public void initialGroupsAreUnlinkedOnDeProvisioning() {
		associateClient(clientId);
		Client client = mixingCore.getClientById(clientId);

		deAssociateClient();

		assertThat(client.getTxGroups()).isEmpty();
		assertThat(client.getRxGroups()).isEmpty();
		assertThat(mixingCore.getClientById(clientId)).isNull();
	}
}
