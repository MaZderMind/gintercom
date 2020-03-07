package de.mazdermind.gintercom.mixingcore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.mixingcore.tools.MixingCoreTestManager;
import de.mazdermind.gintercom.mixingcore.tools.PanelAndClient;

public class MixingCoreIT {
	private MixingCoreTestManager testManager;

	@Before
	public void before() {
		testManager = MixingCoreTestManager.getInstance();
	}

	@After
	public void after() {
		testManager.cleanup();
	}

	/**
	 * 1 Group, 1 Panel
	 * Panel 1 transmits to Group 1
	 * Panel 1 receives from Group 1
	 * assert that Panel 1 hears itself
	 */
	@Test
	public void panelTransmittingIntoAGroupItIsAlsoReceivingFromHearsItsOwnAudio() {
		Group group1 = testManager.addGroup("1");
		PanelAndClient panel1 = testManager.addPanel("1");

		panel1.getPanel().startTransmittingTo(group1);
		panel1.getPanel().startReceivingFrom(group1);

		panel1.getClient().enableSine(800.);
		panel1.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));
	}

	/**
	 * 1 Group, 2 Panels
	 * Panel 1 transmits to Group 1
	 * Panel 2 receives from Group 1
	 * assert that Panel 2 hears Panel 1
	 */
	@Test
	public void panelReceivingFromAGroupHearsAudioTransmittedFromAnotherPanelIntoThisGroup() {
		Group group1 = testManager.addGroup("1");
		PanelAndClient panel1 = testManager.addPanel("1");
		PanelAndClient panel2 = testManager.addPanel("2");

		panel1.getPanel().startTransmittingTo(group1);
		panel2.getPanel().startReceivingFrom(group1);

		panel1.getClient().enableSine(2000.);
		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2000.));
		panel1.getClient().getAudioAnalyser().awaitSilence();
	}

	/**
	 * 1 Group, 3 Panels
	 * Panel 1 transmits to Group 1
	 * Panel 2 receives from Group 1
	 * Panel 3 receives from Group 1
	 * assert that Panel 2 hears Panel 1
	 * Panel 3 joins
	 * assert that Panel 3 also hears Panel 1
	 * assert that Panel 2 still hears Panel 1
	 * Panel 3 leaves
	 * assert that Panel 2 still hears Panel 1
	 */
	@Test
	public void panelCanJoinAndLeaveGroupWithoutDisturbingOtherPanels() {
		Group group1 = testManager.addGroup("1");
		PanelAndClient panel1 = testManager.addPanel("1");
		PanelAndClient panel2 = testManager.addPanel("2");

		panel1.getPanel().startTransmittingTo(group1);
		panel2.getPanel().startReceivingFrom(group1);

		panel1.getClient().enableSine(2500.);
		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));

		PanelAndClient panel3 = testManager.addPanel("3");
		panel3.getPanel().startReceivingFrom(group1);
		panel3.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));

		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));

		panel3.stopAndRemove();

		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));
	}

	/**
	 * 2 Groups, 2 Panels
	 * Panel 1 receives from Group 1
	 * Panel 1 transmits to Group 2
	 * Panel 2 transmits to Group 1
	 * Panel 2 receives from Group 2
	 * assert that the panels hear each other but not them self
	 */
	@Test
	public void panelTransmittingIntoAGroupItIsNotReceivingFromDoesNotHearItsOwnAudio() {
		Group group1 = testManager.addGroup("1");
		Group group2 = testManager.addGroup("2");

		PanelAndClient panel1 = testManager.addPanel("1");
		PanelAndClient panel2 = testManager.addPanel("2");

		panel1.getPanel().startReceivingFrom(group1);
		panel1.getPanel().startTransmittingTo(group2);

		panel2.getPanel().startReceivingFrom(group2);
		panel2.getPanel().startTransmittingTo(group1);

		panel1.getClient().enableSine(800.);
		panel2.getClient().enableSine(400.);

		panel1.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(400.));
		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));
	}

	/**
	 * 2 Groups, 3 Panel
	 * Panel 1 transmits to Group 1, Group 2
	 * Panel 2 receives from Group 1
	 * Panel 3 receives from Group 2
	 * assert that Panel 2 and Panel 3 both hear Panel 1
	 */
	@Test
	public void panelTransmittingIntoMultipleGroupsIsHeardInAllOfThem() {
		Group group1 = testManager.addGroup("1");
		Group group2 = testManager.addGroup("2");

		PanelAndClient panel1 = testManager.addPanel("1");
		PanelAndClient panel2 = testManager.addPanel("2");
		PanelAndClient panel3 = testManager.addPanel("3");

		panel1.getPanel().startTransmittingTo(group1);
		panel1.getPanel().startTransmittingTo(group2);
		panel1.getClient().enableSine(600.);

		panel2.getPanel().startReceivingFrom(group1);
		panel3.getPanel().startReceivingFrom(group2);

		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));
		panel3.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));
	}


	/**
	 * 2 Groups, 3 Panel
	 * Panel 1 receives from Group 1, Group 2
	 * Panel 2 transmits to Group 1
	 * Panel 3 transmits to Group 2
	 * assert that Panel 1 hears both Panel 2 and Panel 3
	 */
	@Test
	public void panelReceivingMultipleGroupsHearsAudioFromAllOfThem() {
		Group group1 = testManager.addGroup("1");
		Group group2 = testManager.addGroup("2");

		PanelAndClient panel1 = testManager.addPanel("1");
		PanelAndClient panel2 = testManager.addPanel("2");
		PanelAndClient panel3 = testManager.addPanel("3");

		panel1.getPanel().startReceivingFrom(group1);
		panel1.getPanel().startReceivingFrom(group2);

		panel2.getPanel().startTransmittingTo(group1);
		panel3.getPanel().startTransmittingTo(group2);

		panel2.getClient().enableSine(1000.);
		panel3.getClient().enableSine(2000.);

		panel1.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(1000., 2000.));
	}

	/**
	 * 2 Groups, 4 Panels
	 * Panel 1 transmits to Group 1
	 * Panel 2 receives from Group 1
	 * Panel 3 transmits to Group 2
	 * Panel 4 transmits to Group 2
	 * assert that Panel 2 hears Panel 1 and Panel 4 hears Panel 3 but nothing else
	 */
	@Test
	public void panelsCanCommunicateInParallel() {
		Group group1 = testManager.addGroup("1");
		Group group2 = testManager.addGroup("2");

		PanelAndClient panel1 = testManager.addPanel("1");
		PanelAndClient panel2 = testManager.addPanel("2");
		PanelAndClient panel3 = testManager.addPanel("3");
		PanelAndClient panel4 = testManager.addPanel("4");

		panel1.getPanel().startTransmittingTo(group1);
		panel2.getPanel().startTransmittingTo(group2);
		panel3.getPanel().startReceivingFrom(group1);
		panel4.getPanel().startReceivingFrom(group2);

		panel1.getClient().enableSine(400.);
		panel2.getClient().enableSine(600.);

		panel3.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(400.));
		panel4.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));
	}
}
