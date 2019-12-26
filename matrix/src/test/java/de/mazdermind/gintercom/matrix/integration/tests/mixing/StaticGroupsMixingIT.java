package de.mazdermind.gintercom.matrix.integration.tests.mixing;

import static de.mazdermind.gintercom.matrix.integration.tools.builder.RandomPanelConfigBuilder.randomPanelConfig;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.matrix.integration.tools.builder.RtpTestClientRegisterer;
import de.mazdermind.gintercom.matrix.integration.tools.rtp.RtpTestClient;

public class StaticGroupsMixingIT extends IntegrationTestBase {
	private static final Logger log = LoggerFactory.getLogger(StaticGroupsMixingIT.class);

	@Autowired
	private RtpTestClientRegisterer clientRegisterer;

	/**
	 * 1 Group, 1 Panel
	 * Panel 1 txGroups = Group 1
	 * Panel 1 rxGroups = Group 1
	 * assert that Panel 1 hears itself
	 */
	@Test
	public void panelTransmittingIntoAGroupItIsAlsoReceivingFromHearsItsOwnAudio() throws InterruptedException {
		RtpTestClient client1 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("1")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay()))
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		log.info("assert that Panel 1 hears itself");
		client1
			.enableSine(650)
			.getAudioAnalyser()
			.awaitFrequencies(ImmutableSet.of(650.));

		clientRegisterer.stopAndDeregisterTestClient(client1);
	}

	/**
	 * 1 Group, 2 Panels
	 * Panel 1 txGroups = Group 1
	 * Panel 2 rxGroups = Group 1
	 * assert that Panel 2 hears Panel 1
	 */
	@Test
	public void panelReceivingFromAGroupHearsAudioTransmittedFromAnotherPanelIntoThisGroup() {
		RtpTestClient client1 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("1")
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		RtpTestClient client2 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("2")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		client1.enableSine(800);

		log.info("assert that Panel 2 hears Panel 1");
		client2.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));

		clientRegisterer.stopAndDeregisterTestClients(client1, client2);
	}

	/**
	 * 1 Group, 3 Panels
	 * Panel 1 txGroups = Group 1
	 * Panel 2 rxGroups = Group 1
	 * Panel 3 rxGroups = Group 1
	 * assert that Panel 2 hears Panel 1
	 * Panel 3 joins
	 * assert that Panel 3 also hears Panel 1
	 * assert that Panel 2 still hears Panel 1
	 * Panel 3 leaves
	 * assert that Panel 2 still hears Panel 1
	 */
	@Test
	public void panelCanJoinAndLeaveGroupWithoutDisturbingOtherPanels() {
		RtpTestClient client1 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("1")
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		RtpTestClient client2 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("2")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		client1.enableSine(800);

		log.info("assert that Panel 2 hears Panel 1");
		client2.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));

		log.info("Panel 3 joins");
		RtpTestClient client3 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("3")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		log.info("assert that Panel 3 also hears Panel 1");
		client3.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));

		log.info("assert that Panel 2 still hears Panel 1");
		client2.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));

		log.info("Panel 3 leaves");
		clientRegisterer.deregisterTestClient(client3);

		log.info("assert that Panel 3 receives more data");
		client3
			.getAudioAnalyser()
			.awaitNoData();

		client3.stop();

		log.info("assert that Panel 2 still hears Panel 1");
		client2.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));

		clientRegisterer.stopAndDeregisterTestClients(client1, client2);
	}

	/**
	 * 2 Groups, 2 Panels
	 * Panel 1 rxGroups = Group 1
	 * Panel 1 txGroups = Group 2
	 * Panel 2 txGroups = Group 1
	 * Panel 2 rxGroups = Group 2
	 * assert that the panels hear each other but not them self
	 * <p>
	 * FIXME there is a Problem within GStreamer when two spectrum elements are used in one pipeline which affects this test
	 * https://gitlab.freedesktop.org/gstreamer/gst-plugins-good/issues/682
	 */
	@Test
	public void panelTransmittingIntoAGroupItIsNotReceivingFromDoesNotHearItsOwnAudio() {
		RtpTestClient client1 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("1")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay()))
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_2.getDisplay())));

		RtpTestClient client2 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("2")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_2.getDisplay()))
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		log.info("Starting Clients");
		client1.enableSine(400);
		client2.enableSine(5000);

		log.info("Awaiting client 1");
		client1.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(5000.));

		log.info("Awaiting client 2");
		client2.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(400.));

		log.info("Awaiting client 1");
		client1.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(5000.));

		clientRegisterer.stopAndDeregisterTestClients(client1, client2);
	}

	/**
	 * 2 Groups, 3 Panel
	 * Panel 1 rxGroups = Group 1, Group 2
	 * Panel 2 txGroups = Group 1
	 * Panel 3 txGroups = Group 2
	 * assert that Panel 1 hears both Panel 2 and Panel 3
	 */
	@Test
	public void panelReceivingMultipleGroupsHearsAudioFromAllOfThem() throws InterruptedException {
		RtpTestClient client1 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("1")
			.setRxGroups(ImmutableSet.of(
				TestConfig.GROUP_TEST_1.getDisplay(),
				TestConfig.GROUP_TEST_2.getDisplay()
			)));

		RtpTestClient client2 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("2")
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		RtpTestClient client3 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("3")
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_2.getDisplay())));

		client2.enableSine(600);
		client3.enableSine(900);

		client1.start().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600., 900.));

		clientRegisterer.stopAndDeregisterTestClients(client1, client2, client3);
	}

	/**
	 * 2 Groups, 3 Panel
	 * Panel 1 txGroups = Group 1, Group 2
	 * Panel 2 rxGroups = Group 1
	 * Panel 3 rxGroups = Group 2
	 * assert that Panel 2 and Panel 3 both hear Panel 1
	 */
	@Test
	public void panelTransmittingIntoMultipleGroupsIsHeardInAllOfThem() {
		RtpTestClient client1 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("1")
			.setTxGroups(ImmutableSet.of(
				TestConfig.GROUP_TEST_1.getDisplay(),
				TestConfig.GROUP_TEST_2.getDisplay()
			)));

		RtpTestClient client2 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("2")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		RtpTestClient client3 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("3")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_2.getDisplay())));

		client1.enableSine(1000);

		client2.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(1000.));
		client3.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(1000.));
		client1.getAudioAnalyser().awaitSilence();

		clientRegisterer.stopAndDeregisterTestClients(client1, client2, client3);
	}

	/**
	 * 2 Groups, 4 Panels
	 * Panel 1 txGroups = Group 1
	 * Panel 2 rxGroups = Group 1
	 * Panel 3 txGroups = Group 2
	 * Panel 4 txGroups = Group 2
	 * assert that Panel 2 hears Panel 1 and Panel 4 hears Panel 3 but nothing else
	 * <p>
	 * FIXME there is a Problem within GStreamer when two spectrum elements are used in one pipeline which affects this test
	 * https://gitlab.freedesktop.org/gstreamer/gst-plugins-good/issues/682
	 */
	@Test
	public void panelsCanCommunicateInParallel() {
		RtpTestClient client1 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("1")
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		RtpTestClient client2 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("2")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		RtpTestClient client3 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("3")
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_2.getDisplay())));

		RtpTestClient client4 = clientRegisterer.registerTestClient(randomPanelConfig()
			.setDisplay("4")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_2.getDisplay())));

		client1.enableSine(200);
		client3.enableSine(600);

		client2.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(200.));
		client4.getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));

		clientRegisterer.stopAndDeregisterTestClients(client1, client2, client3, client4);
	}
}
