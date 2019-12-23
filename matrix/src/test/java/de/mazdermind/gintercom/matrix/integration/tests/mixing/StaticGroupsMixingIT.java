package de.mazdermind.gintercom.matrix.integration.tests.mixing;

import static com.oblac.nomen.Nomen.randomName;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import java.net.InetAddress;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelDeRegistrationEvent;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelRegistrationEvent;
import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.matrix.integration.tools.builder.RandomPanelConfigBuilder;
import de.mazdermind.gintercom.matrix.integration.tools.rtp.RtpTestClient;
import de.mazdermind.gintercom.matrix.pipeline.Pipeline;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.portpool.PortSet;

public class StaticGroupsMixingIT extends IntegrationTestBase {
	private static final Logger log = LoggerFactory.getLogger(StaticGroupsMixingIT.class);

	@Autowired
	private Pipeline pipeline;

	@Autowired
	private PortAllocationManager portAllocationManager;

	@Test
	public void panelTransmittingIntoAGroupItIsAlsoReceivingFromHearsItsOwnAudio() throws InterruptedException {
		String panelId = randomName();
		String hostId = String.format("%s-%s", randomNumeric(4), randomNumeric(4));

		PortSet portSet = portAllocationManager.allocatePortSet(hostId);
		PanelConfig panelConfig = RandomPanelConfigBuilder.randomPanelConfig()
			.setHostId(hostId)
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay()))
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay()));

		pipeline.handlePanelRegistration(new PanelRegistrationEvent(panelId, panelConfig, portSet, InetAddress.getLoopbackAddress()));

		//Thread.sleep(250); // prevents the problem for a while (250 test runs)

		new RtpTestClient(portSet)
			.enableSine(650)
			.awaitPeaks(ImmutableList.of(650))
			.stop();

		pipeline.handlePanelDeRegistration(new PanelDeRegistrationEvent(panelId));

		// 1 Group, 1 Panel
		// Panel 1 txGroups = Group 1
		// Panel 1 rxGroups = Group 1
		// assert that Panel 1 hears itself
	}

	@Test
	@Ignore("Not yet implemented")
	public void panelReceivingFromAGroupHearsAudioTransmittedFromAnotherPanelIntoThisGroup() {
		// 1 Group, 2 Panels
		// Panel 1 txGroups = Group 1
		// Panel 2 rxGroups = Group 1
		// assert that Panel 2 hears Panel 1
	}

	@Test
	@Ignore("Not yet implemented")
	public void panelCanJoinAndLeaveGroupWithoutDisturbingOtherPanels() {
		// 1 Group, 3 Panels
		// Panel 1 txGroups = Group 1
		// Panel 2 rxGroups = Group 1
		// Panel 3 rxGroups = Group 1
		// assert that Panel 2 hears Panel 1
		// Panel 3 joins
		// assert that Panel 2 hears Panel 1
		// Panel 3 leaves
		// assert that Panel 2 hears Panel 1
	}

	@Test
	@Ignore("Not yet implemented")
	public void panelTransmittingIntoAGroupItIsNotReceivingFromDoesNotHearItsOwnAudio() {
		// 2 Groups, 1 Panel
		// Panel 1 rxGroups = Group 1
		// Panel 1 txGroups = Group 2
		// assert that Panel 1 heard nothing, despite transmitting audio
	}

	@Test
	@Ignore("Not yet implemented")
	public void panelReceivingMultipleGroupsHearsAudioFromAllOfThem() {
		// 2 Groups, 3 Panel
		// Panel 1 rxGroups = Group 1, Group 2
		// Panel 2 txGroups = Group 1
		// Panel 3 txGroups = Group 2
		// assert that Panel 1 hears both Panel 2 and Panel 3
	}

	@Test
	@Ignore("Not yet implemented")
	public void panelTransmittingIntoMultipleGroupsIsHeardInAllOfThem() {
		// 2 Groups, 3 Panel
		// Panel 1 txGroups = Group 1, Group 2
		// Panel 2 rxGroups = Group 1
		// Panel 3 rxGroups = Group 2
		// assert that Panel 2 and Panel 3 both hear Panel 1
	}

	@Test
	@Ignore("Not yet implemented")
	public void panelsCanCommunicateInParallel() {
		// 2 Groups, 4 Panels
		// Panel 1 txGroups = Group 1
		// Panel 2 rxGroups = Group 1
		// Panel 3 txGroups = Group 2
		// Panel 4 txGroups = Group 2
		// assert that Panel 2 hears Panel 1 and Panel 4 hears Panel 3 but nothing else
	}
}
