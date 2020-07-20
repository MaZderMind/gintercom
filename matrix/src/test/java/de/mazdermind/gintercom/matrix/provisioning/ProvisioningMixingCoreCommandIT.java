package de.mazdermind.gintercom.matrix.provisioning;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;
import de.mazdermind.gintercom.mixingcore.Client;
import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;

public class ProvisioningMixingCoreCommandIT extends ControlServerTestBase {
	private static final String PANEL_ID = "THE_PANEL_ID";
	private static final String RX_GROUP = "THE_RX_GROUP";
	private static final String TX_GROUP_1 = "THE_TX_GROUP_1";
	private static final String TX_GROUP_2 = "THE_TX_GROUP_2";

	@Autowired
	private TestConfig testConfig;

	@Autowired
	private MixingCore mixingCore;

	@Before
	public void prepareConfig() {
		PanelConfig panelConfig = new PanelConfig()
			.setHostId(HOST_ID)
			.setDisplay("THE_DISPLAY_NAME")
			.setRxGroups(ImmutableSet.of(RX_GROUP))
			.setTxGroups(ImmutableSet.of(TX_GROUP_1, TX_GROUP_2));

		testConfig.getPanels().put(PANEL_ID, panelConfig);

		mixingCore.addGroup(RX_GROUP);
		mixingCore.addGroup(TX_GROUP_1);
		mixingCore.addGroup(TX_GROUP_2);
	}

	@Test
	public void initialGroupsAreLinkedOnProvisioning() {
		associateClient();

		Client client = mixingCore.getClientByName(HOST_ID);
		assertThat(client.getRxGroups())
			.extracting(Group::getName)
			.containsOnly(RX_GROUP);

		assertThat(client.getTxGroups())
			.extracting(Group::getName)
			.containsOnly(TX_GROUP_1, TX_GROUP_2);
	}

	@Test
	public void initialGroupsAreUnlinkedOnDeProvisioning() {
		associateClient();
		Client client = mixingCore.getClientByName(HOST_ID);

		deAssociateClient();

		assertThat(client.getTxGroups()).isEmpty();
		assertThat(client.getRxGroups()).isEmpty();
		assertThat(mixingCore.getClientByName(HOST_ID)).isNull();
	}
}
