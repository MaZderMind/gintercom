package de.mazdermind.gintercom.matrix.integration.tests.mixing;

import static de.mazdermind.gintercom.matrix.integration.tools.builder.RandomPanelRegistrationMessageBuilder.randomPanelRegistrationMessageForPanelConfig;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;

public class StaticGroupsMixingIT extends IntegrationTestBase {
	@Autowired
	private TestConfig testConfig;
	private PanelConfig panelConfig;
	private PanelRegistrationMessage panelRegistrationMessage;
	private GroupConfig groupConfig;

	@Before
	public void prepare() {
		testConfig.reset();

		panelConfig = testConfig.addRandomPanel();
		panelRegistrationMessage = randomPanelRegistrationMessageForPanelConfig(panelConfig);

		groupConfig = testConfig.addRandomGroup();
	}

	@Test
	@Ignore("Not yet implemented")
	public void panelTransmittingIntoAGroupItIsAlsoReceivingFromHearsItsOwnAudio() {
		// 1 Group, 1 Panel
		// rxGroups = txGroups = Group
		// assert Panel 1 hears itself
	}

	@Test
	@Ignore("Not yet implemented")
	public void panelReceivingFromAGroupHearsAudioTransmittedFromAnotherPanelIntoThisGroup() {
		// 1 Group, 2 Panels
		// Panel 1 rxGroups = Group
		// Panel 2 txGroups = Group
		// assert Panel 2 hears what Panel 1 transmits
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
