package de.mazdermind.gintercom.mixingcore.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Panel;
import de.mazdermind.gintercom.mixingcore.it.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.it.tools.IntegrationTestBase;
import de.mazdermind.gintercom.mixingcore.it.tools.MixingCoreTestManager;
import de.mazdermind.gintercom.mixingcore.it.tools.rtp.RtpTestClient;

public class DataTransmissionIT extends IntegrationTestBase {
	private static final String PANEL_ID = "data";

	private RtpTestClient client;
	private PortSet ports;
	private Panel panel;
	private MixingCore mixingCore;

	@Before
	public void setupPanel() {
		ports = testManager.getPortSetPool().getNextPortSet();
		mixingCore = testManager.getMixingCore();
		client = new RtpTestClient(ports, PANEL_ID);
	}

	@After
	public void cleanupPanelAndClient() {
		if (client != null) {
			client.stop();
		}

		if (panel != null) {
			mixingCore.removePanel(panel);
		}
	}

	@Test
	public void doesNotReceiveDataInitially() {
		client.start();
		client.getAudioAnalyser().awaitNoData();
	}

	@Test
	public void receivesDataAfterPanelAdded() {
		client.getAudioAnalyser().awaitNoData();

		panel = testManager.getMixingCore().addPanel(
			PANEL_ID, MixingCoreTestManager.MATRIX_HOST,
			ports.getPanelToMatrix(), ports.getMatrixToPanel());

		client.getAudioAnalyser().awaitData();
	}

	@Test
	public void doesNotReceiveDataAfterPanelRemoved() {
		client.getAudioAnalyser().awaitNoData();

		panel = testManager.getMixingCore().addPanel(
			PANEL_ID, MixingCoreTestManager.MATRIX_HOST,
			ports.getPanelToMatrix(), ports.getMatrixToPanel());

		client.getAudioAnalyser().awaitData();

		mixingCore.removePanel(panel);
		panel = null;

		client.getAudioAnalyser().awaitNoData();
	}

	@Test
	public void receivesAudioDataAfterAddedAgain() {
		client.getAudioAnalyser().awaitNoData();

		panel = testManager.getMixingCore().addPanel(
			PANEL_ID, MixingCoreTestManager.MATRIX_HOST,
			ports.getPanelToMatrix(), ports.getMatrixToPanel());

		client.getAudioAnalyser().awaitData();

		mixingCore.removePanel(panel);
		panel = null;

		client.getAudioAnalyser().awaitNoData();

		panel = testManager.getMixingCore().addPanel(
			PANEL_ID, MixingCoreTestManager.MATRIX_HOST,
			ports.getPanelToMatrix(), ports.getMatrixToPanel());

		client.getAudioAnalyser().awaitData();
	}
}