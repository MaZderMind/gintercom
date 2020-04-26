package de.mazdermind.gintercom.mixingcore.it;

import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Panel;
import de.mazdermind.gintercom.mixingcore.exception.InvalidOperationException;
import de.mazdermind.gintercom.mixingcore.it.tools.IntegrationTestBase;
import de.mazdermind.gintercom.mixingcore.it.tools.PanelAndClient;

/**
 * This Test ensures that the Pipeline does not crash when the API is used in an invalid way and that other Panels and Groups are still
 * operable. The Mis-Used Panels and Groups are not expected to be functional after misusing the API with them.
 */
public class InvalidApiUsageIT extends IntegrationTestBase {
	private Panel panel;
	private Group group;
	private MixingCore mixingCore;

	@Before
	public void before() {
		panel = testManager.addPanel("p").getPanel();
		group = testManager.addGroup("g");
		mixingCore = testManager.getMixingCore();
	}

	@Test
	public void toleratesInvalidOrderOfOperation() {
		assertThrows(InvalidOperationException.class, () -> panel.stopTransmittingTo(group));
		assertThrows(InvalidOperationException.class, () -> panel.stopTransmittingTo(group));

		assertThrows(InvalidOperationException.class, () -> panel.stopReceivingFrom(group));
		assertThrows(InvalidOperationException.class, () -> panel.stopReceivingFrom(group));

		ensurePipelineIsStillFunctional();
	}

	@Test
	public void toleratesMultipleRemoval() {
		mixingCore.removePanel(panel);
		assertThrows(InvalidOperationException.class, () -> mixingCore.removePanel(panel));

		mixingCore.removeGroup(group);
		assertThrows(InvalidOperationException.class, () -> mixingCore.removeGroup(group));

		ensurePipelineIsStillFunctional();
	}

	@Test
	public void toleratesDuplicateNames() {
		assertThrows(InvalidOperationException.class, () -> testManager.addPanel("p"));
		assertThrows(InvalidOperationException.class, () -> testManager.addGroup("g"));

		ensurePipelineIsStillFunctional();
	}

	private void ensurePipelineIsStillFunctional() {
		PanelAndClient newPanel = testManager.addPanel("p-new");
		Group newGroup = testManager.addGroup("g-new");

		newPanel.getPanel().startReceivingFrom(newGroup);
		newPanel.getPanel().startTransmittingTo(newGroup);

		newPanel.getClient().enableSine(880.);
		newPanel.getClient().getAudioAnalyser().awaitFrequencies(ImmutableSet.of(880.));
	}
}
