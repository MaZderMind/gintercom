package de.mazdermind.gintercom.mixingcore.integration.tests;

import org.freedesktop.gstreamer.Gst;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.MixingCoreFactory;
import de.mazdermind.gintercom.mixingcore.Panel;
import de.mazdermind.gintercom.mixingcore.integration.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.integration.portpool.PortSetPool;
import de.mazdermind.gintercom.mixingcore.integration.portpool.PortSetPoolFactory;
import de.mazdermind.gintercom.mixingcore.integration.tools.rtp.RtpTestClient;

public class MixingIT {
	private MixingCore mixingCore;
	private PortSetPool portSetPool;

	@Before
	public void before() {
		Gst.init();
		mixingCore = MixingCoreFactory.getInstance();
		portSetPool = PortSetPoolFactory.getInstance();
	}

	/**
	 * 1 Group, 1 Panel
	 * Panel 1 txGroups = Group 1
	 * Panel 1 rxGroups = Group 1
	 * assert that Panel 1 hears itself
	 */
	@Test
	public void panelTransmittingIntoAGroupItIsAlsoReceivingFromHearsItsOwnAudio() {
		Group group1 = mixingCore.addGroup("1");
		PanelAndClient panel1 = new PanelAndClient("1");

		panel1.getPanel().startTransmittingTo(group1);
		panel1.getPanel().startReceivingFrom(group1);

		panel1.getClient().enableSine(800.);
		panel1.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));

		panel1.getClient().stop();
		panel1.getPanel().remove();
		group1.remove();
	}

	/**
	 * 1 Group, 2 Panels
	 * Panel 1 txGroups = Group 1
	 * Panel 2 rxGroups = Group 1
	 * assert that Panel 2 hears Panel 1
	 */
	@Test
	public void panelReceivingFromAGroupHearsAudioTransmittedFromAnotherPanelIntoThisGroup() {
		Group group1 = mixingCore.addGroup("1");
		PanelAndClient panel1 = new PanelAndClient("1");
		PanelAndClient panel2 = new PanelAndClient("2");

		panel1.getPanel().startTransmittingTo(group1);
		panel2.getPanel().startReceivingFrom(group1);

		panel1.getClient().enableSine(2000.);
		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2000.));

		panel1.stopAndRemove();
		panel2.stopAndRemove();

		group1.remove();
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
		Group group1 = mixingCore.addGroup("1");
		PanelAndClient panel1 = new PanelAndClient("1");
		PanelAndClient panel2 = new PanelAndClient("2");

		panel1.getPanel().startTransmittingTo(group1);
		panel2.getPanel().startReceivingFrom(group1);

		panel1.getClient().enableSine(2500.);
		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));

		PanelAndClient panel3 = new PanelAndClient("3");
		panel3.getPanel().startReceivingFrom(group1);
		panel3.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));

		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));

		panel3.stopAndRemove();

		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(2500.));

		panel1.stopAndRemove();
		panel2.stopAndRemove();

		group1.remove();
	}

	/**
	 * 2 Groups, 2 Panels
	 * Panel 1 rxGroups = Group 1
	 * Panel 1 txGroups = Group 2
	 * Panel 2 txGroups = Group 1
	 * Panel 2 rxGroups = Group 2
	 * assert that the panels hear each other but not them self
	 */
	@Test
	public void panelTransmittingIntoAGroupItIsNotReceivingFromDoesNotHearItsOwnAudio() {
		Group group1 = mixingCore.addGroup("1");
		Group group2 = mixingCore.addGroup("2");

		PanelAndClient panel1 = new PanelAndClient("1");
		PanelAndClient panel2 = new PanelAndClient("2");

		panel1.getPanel().startReceivingFrom(group1);
		panel1.getPanel().startTransmittingTo(group2);

		panel2.getPanel().startReceivingFrom(group2);
		panel2.getPanel().startTransmittingTo(group1);

		panel1.getClient().enableSine(800.);
		panel2.getClient().enableSine(400.);

		panel1.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(400.));
		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(800.));

		panel1.stopAndRemove();
		panel2.stopAndRemove();

		group1.remove();
		group2.remove();
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
		Group group1 = mixingCore.addGroup("1");
		Group group2 = mixingCore.addGroup("2");

		PanelAndClient panel1 = new PanelAndClient("1");
		PanelAndClient panel2 = new PanelAndClient("2");
		PanelAndClient panel3 = new PanelAndClient("3");

		panel1.getPanel().startTransmittingTo(group1);
		panel1.getPanel().startTransmittingTo(group2);
		panel1.getClient().enableSine(600.);

		panel2.getPanel().startReceivingFrom(group1);
		panel3.getPanel().startReceivingFrom(group2);

		panel2.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));
		panel3.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));

		panel1.stopAndRemove();
		panel2.stopAndRemove();
		panel3.stopAndRemove();

		group1.remove();
		group2.remove();
	}


	/**
	 * 2 Groups, 3 Panel
	 * Panel 1 rxGroups = Group 1, Group 2
	 * Panel 2 txGroups = Group 1
	 * Panel 3 txGroups = Group 2
	 * assert that Panel 1 hears both Panel 2 and Panel 3
	 */
	@Test
	public void panelReceivingMultipleGroupsHearsAudioFromAllOfThem() {
		Group group1 = mixingCore.addGroup("1");
		Group group2 = mixingCore.addGroup("2");

		PanelAndClient panel1 = new PanelAndClient("1");
		PanelAndClient panel2 = new PanelAndClient("2");
		PanelAndClient panel3 = new PanelAndClient("3");

		panel1.getPanel().startReceivingFrom(group1);
		panel1.getPanel().startReceivingFrom(group2);

		panel2.getPanel().startTransmittingTo(group1);
		panel3.getPanel().startTransmittingTo(group2);

		panel2.getClient().enableSine(1000.);
		panel3.getClient().enableSine(2000.);

		panel1.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(1000., 2000.));

		panel1.stopAndRemove();
		panel2.stopAndRemove();
		panel3.stopAndRemove();

		group1.remove();
		group2.remove();
	}

	/**
	 * 2 Groups, 4 Panels
	 * Panel 1 txGroups = Group 1
	 * Panel 2 rxGroups = Group 1
	 * Panel 3 txGroups = Group 2
	 * Panel 4 txGroups = Group 2
	 * assert that Panel 2 hears Panel 1 and Panel 4 hears Panel 3 but nothing else
	 */
	@Test
	public void panelsCanCommunicateInParallel() {
		Group group1 = mixingCore.addGroup("1");
		Group group2 = mixingCore.addGroup("2");

		PanelAndClient panel1 = new PanelAndClient("1");
		PanelAndClient panel2 = new PanelAndClient("2");
		PanelAndClient panel3 = new PanelAndClient("3");
		PanelAndClient panel4 = new PanelAndClient("4");

		panel1.getPanel().startTransmittingTo(group1);
		panel2.getPanel().startTransmittingTo(group2);
		panel3.getPanel().startReceivingFrom(group1);
		panel4.getPanel().startReceivingFrom(group2);

		panel1.getClient().enableSine(400.);
		panel2.getClient().enableSine(600.);

		panel3.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(400.));
		panel4.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(600.));

		panel1.stopAndRemove();
		panel2.stopAndRemove();
		panel3.stopAndRemove();
		panel4.stopAndRemove();

		group1.remove();
		group2.remove();
	}

	private class PanelAndClient {
		private Panel panel;
		private RtpTestClient client;

		public PanelAndClient(String name) {
			PortSet ports = portSetPool.getNextPortSet();
			panel = mixingCore.addPanel(name, "127.0.0.1", ports.getPanelToMatrix(), ports.getMatrixToPanel());
			client = new RtpTestClient(ports, name);
		}

		public Panel getPanel() {
			return panel;
		}

		public RtpTestClient getClient() {
			return client;
		}

		public void stopAndRemove() {
			client.stop();
			panel.remove();
		}
	}
}
