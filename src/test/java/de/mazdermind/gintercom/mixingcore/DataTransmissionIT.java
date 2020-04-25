package de.mazdermind.gintercom.mixingcore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.mixingcore.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.tools.IntegrationTestBase;
import de.mazdermind.gintercom.mixingcore.tools.MixingCoreTestManager;
import de.mazdermind.gintercom.mixingcore.tools.rtp.RtpTestClient;

public class DataTransmissionIT extends IntegrationTestBase {
	private static final String PANEL_ID = "data";

	private RtpTestClient client;
	private PortSet ports;
	private Panel panel;

	@Before
	public void setupPanel() {
		ports = testManager.getPortSetPool().getNextPortSet();
		client = new RtpTestClient(ports, PANEL_ID);
	}

	@After
	public void cleanupPanelAndClient() {
		if (client != null) {
			client.stop();
		}

		if (panel != null) {
			panel.remove();
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

		panel.remove();
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

		panel.remove();
		panel = null;

		client.getAudioAnalyser().awaitNoData();

		panel = testManager.getMixingCore().addPanel(
			PANEL_ID, MixingCoreTestManager.MATRIX_HOST,
			ports.getPanelToMatrix(), ports.getMatrixToPanel());

		client.getAudioAnalyser().awaitData();
	}
}
