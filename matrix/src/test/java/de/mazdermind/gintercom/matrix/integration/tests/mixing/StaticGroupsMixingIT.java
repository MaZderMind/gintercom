package de.mazdermind.gintercom.matrix.integration.tests.mixing;

import static de.mazdermind.gintercom.matrix.integration.tools.builder.RandomPanelConfigBuilder.randomPanelConfig;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelRegistrationEvent;
import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.matrix.integration.tools.builder.PanelRegistrationEventBuilder;
import de.mazdermind.gintercom.matrix.integration.tools.rtp.RtpTestClient;
import de.mazdermind.gintercom.matrix.pipeline.Pipeline;

public class StaticGroupsMixingIT extends IntegrationTestBase {
	private static final Logger log = LoggerFactory.getLogger(StaticGroupsMixingIT.class);

	@Autowired
	private Pipeline pipeline;

	@Autowired
	private PanelRegistrationEventBuilder panelRegistrationEventBuilder;

	/**
	 * 1 Group, 1 Panel
	 * Panel 1 txGroups = Group 1
	 * Panel 1 rxGroups = Group 1
	 * assert that Panel 1 hears itself
	 */
	@Test
	public void panelTransmittingIntoAGroupItIsAlsoReceivingFromHearsItsOwnAudio() {
		PanelRegistrationEvent panel = panelRegistrationEventBuilder.buildPanelRegistrationEvent(randomPanelConfig()
			.setDisplay("1")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay()))
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		pipeline.handlePanelRegistration(panel);

		log.info("assert that Panel 1 hears itself");
		new RtpTestClient(panel.getPortSet())
			.enableSine(650)
			.awaitPeaks(ImmutableList.of(650))
			.stop();

		pipeline.handlePanelDeRegistration(panelRegistrationEventBuilder.buildPanelDeRegistrationEvent(panel));
	}

	/**
	 * 1 Group, 2 Panels
	 * Panel 1 txGroups = Group 1
	 * Panel 2 rxGroups = Group 1
	 * assert that Panel 2 hears Panel 1
	 */
	@Test
	public void panelReceivingFromAGroupHearsAudioTransmittedFromAnotherPanelIntoThisGroup() {
		PanelRegistrationEvent panel1 = panelRegistrationEventBuilder.buildPanelRegistrationEvent(randomPanelConfig()
			.setDisplay("1")
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		PanelRegistrationEvent panel2 = panelRegistrationEventBuilder.buildPanelRegistrationEvent(randomPanelConfig()
			.setDisplay("2")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		pipeline.handlePanelRegistration(panel1);
		pipeline.handlePanelRegistration(panel2);

		RtpTestClient client1 = new RtpTestClient(panel1.getPortSet())
			.enableSine(800);

		RtpTestClient client2 = new RtpTestClient(panel2.getPortSet())
			.awaitPeaks(ImmutableList.of(800));
		log.info("assert that Panel 2 hears Panel 1");

		client1.stop();
		client2.stop();

		pipeline.handlePanelDeRegistration(panelRegistrationEventBuilder.buildPanelDeRegistrationEvent(panel1));
		pipeline.handlePanelDeRegistration(panelRegistrationEventBuilder.buildPanelDeRegistrationEvent(panel2));
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
		PanelRegistrationEvent panel1 = panelRegistrationEventBuilder.buildPanelRegistrationEvent(randomPanelConfig()
			.setDisplay("1")
			.setTxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		PanelRegistrationEvent panel2 = panelRegistrationEventBuilder.buildPanelRegistrationEvent(randomPanelConfig()
			.setDisplay("2")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));

		PanelRegistrationEvent panel3 = panelRegistrationEventBuilder.buildPanelRegistrationEvent(randomPanelConfig()
			.setDisplay("3")
			.setRxGroups(ImmutableSet.of(TestConfig.GROUP_TEST_1.getDisplay())));


		pipeline.handlePanelRegistration(panel1);
		pipeline.handlePanelRegistration(panel2);

		RtpTestClient client1 = new RtpTestClient(panel1.getPortSet())
			.enableSine(800);

		log.info("assert that Panel 2 hears Panel 1");
		RtpTestClient client2 = new RtpTestClient(panel2.getPortSet())
			.awaitPeaks(ImmutableList.of(800));

		log.info("Panel 3 joins");
		pipeline.handlePanelRegistration(panel3);

		log.info("assert that Panel 3 also hears Panel 1");
		RtpTestClient client3 = new RtpTestClient(panel3.getPortSet())
			.awaitPeaks(ImmutableList.of(800));

		log.info("assert that Panel 2 still hears Panel 1");
		client2.awaitPeaks(ImmutableList.of(800));

		log.info("Panel 3 leaves");
		pipeline.handlePanelDeRegistration(panelRegistrationEventBuilder.buildPanelDeRegistrationEvent(panel3));
		client3.stop();

		log.info("assert that Panel 2 still hears Panel 1");
		client2.awaitPeaks(ImmutableList.of(800));

		client1.stop();
		client2.stop();

		pipeline.handlePanelDeRegistration(panelRegistrationEventBuilder.buildPanelDeRegistrationEvent(panel1));
		pipeline.handlePanelDeRegistration(panelRegistrationEventBuilder.buildPanelDeRegistrationEvent(panel2));
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
