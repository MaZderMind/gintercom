package de.mazdermind.gintercom.matrix.integration.tests.mixing;

import static de.mazdermind.gintercom.matrix.integration.tools.builder.RandomPanelRegistrationMessageBuilder.randomPanelRegistrationMessageForPanelConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.matrix.integration.tools.controlserver.ControlServerTestClient;
import de.mazdermind.gintercom.matrix.integration.tools.rtp.RtpTestClient;
import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;

public class StaticGroupsMixingIT extends IntegrationTestBase {
	private final List<Clients> clients = new ArrayList<Clients>();

	@Autowired
	private TestConfig testConfig;

	@Autowired
	private BeanFactory beanFactory;

	@Before
	public void prepare() {
		testConfig.reset();
	}

	@After
	public void teardown() {
		clients.forEach(clientSet -> {
			clientSet.getRtpClient().cleanup();
			clientSet.getControlServerClient().cleanup();
		});
	}

	@Test
	public void panelTransmittingIntoAGroupItIsAlsoReceivingFromHearsItsOwnAudio() {
		RtpTestClient rtpClient = configurePanelAndConnect(TestConfig.GROUP_TEST_1, TestConfig.GROUP_TEST_1).getRtpClient();

		rtpClient.getTx().transmitSine(800);
		rtpClient.getRx().awaitPeaks(ImmutableList.of(800));
	}

	@Test
	public void panelReceivingFromAGroupHearsAudioTransmittedFromAnotherPanelIntoThisGroup() {
		RtpTestClient rtpClient1 = configurePanelAndConnect(TestConfig.GROUP_TEST_1, TestConfig.GROUP_TEST_1).getRtpClient();
		RtpTestClient rtpClient2 = configurePanelAndConnect(TestConfig.GROUP_TEST_1, TestConfig.GROUP_TEST_1).getRtpClient();

		rtpClient1.getTx().transmitSine(800);
		rtpClient2.getRx().awaitPeaks(ImmutableList.of(800));
	}

	@Test
	public void panelCanJoinAndLeaveGroupWithoutDisturbingOtherPanels() {
		Clients client1 = configurePanelAndConnect(TestConfig.GROUP_TEST_1, TestConfig.GROUP_TEST_1);
		client1.getRtpClient().getTx().transmitSine(800);
		client1.getRtpClient().getRx().awaitPeaks(ImmutableList.of(800));

		Clients client2 = configurePanelAndConnect(TestConfig.GROUP_TEST_1, TestConfig.GROUP_TEST_1);
		client2.getRtpClient().getRx().awaitPeaks(ImmutableList.of(800));
		client2.getControlServerClient().disconnect();

		client1.getRtpClient().getRx().awaitPeaks(ImmutableList.of(800));
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

	private Clients configurePanelAndConnect(GroupConfig rxGroup, GroupConfig txGroup) {
		return configurePanelAndConnect(
			rxGroup == null ? ImmutableSet.of() : ImmutableSet.of(rxGroup),
			txGroup == null ? ImmutableSet.of() : ImmutableSet.of(txGroup)
		);
	}

	private Clients configurePanelAndConnect(Set<GroupConfig> rxGroups, Set<GroupConfig> txGroups) {
		PanelConfig panelConfig = testConfig.addRandomPanel()
			.setRxGroups(rxGroups.stream().map(GroupConfig::getDisplay).collect(Collectors.toSet()))
			.setTxGroups(txGroups.stream().map(GroupConfig::getDisplay).collect(Collectors.toSet()));

		PanelRegistrationMessage registrationMessage = randomPanelRegistrationMessageForPanelConfig(panelConfig);

		ControlServerTestClient controlServerClient = beanFactory.getBean(ControlServerTestClient.class);

		controlServerClient.connect();
		controlServerClient.send("/registration", registrationMessage);

		ProvisionMessage provisionMessage = controlServerClient.awaitMessage("/user/provision", ProvisionMessage.class);

		RtpTestClient rtpClient = beanFactory.getBean(RtpTestClient.class);
		rtpClient.connect(provisionMessage.getProvisioningInformation());

		Clients clientSet = new Clients(rtpClient, controlServerClient);
		clients.add(clientSet);

		return clientSet;
	}

	private static class Clients {
		private RtpTestClient rtpClient;
		private ControlServerTestClient controlServerClient;

		public Clients(RtpTestClient rtpClient, ControlServerTestClient controlServerClient) {
			this.rtpClient = rtpClient;
			this.controlServerClient = controlServerClient;
		}

		public RtpTestClient getRtpClient() {
			return rtpClient;
		}

		public ControlServerTestClient getControlServerClient() {
			return controlServerClient;
		}
	}
}
