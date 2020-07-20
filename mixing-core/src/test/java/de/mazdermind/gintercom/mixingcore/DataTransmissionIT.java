package de.mazdermind.gintercom.mixingcore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.mixingcore.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.tools.IntegrationTestBase;
import de.mazdermind.gintercom.mixingcore.tools.MixingCoreTestManager;
import de.mazdermind.gintercom.mixingcore.tools.rtp.RtpTestClient;

public class DataTransmissionIT extends IntegrationTestBase {
	private static final String CLIENT_ID = "data";

	private RtpTestClient rtpTestClient;
	private PortSet ports;
	private Client client;
	private MixingCore mixingCore;

	@Before
	public void setupClient() {
		ports = testManager.getPortSetPool().getNextPortSet();
		mixingCore = testManager.getMixingCore();
		rtpTestClient = new RtpTestClient(ports, CLIENT_ID);
	}

	@After
	public void cleanupClient() {
		if (rtpTestClient != null) {
			rtpTestClient.stop();
		}

		if (client != null) {
			mixingCore.removeClient(client);
		}
	}

	@Test
	public void doesNotReceiveDataInitially() {
		rtpTestClient.start();
		rtpTestClient.getAudioAnalyser().awaitNoData();
	}

	@Test
	public void receivesDataAfterClientAdded() {
		rtpTestClient.getAudioAnalyser().awaitNoData();

		client = testManager.getMixingCore().addClient(
			CLIENT_ID, MixingCoreTestManager.MATRIX_HOST,
			ports.getClientToMatrix(), ports.getMatrixToClient());

		rtpTestClient.getAudioAnalyser().awaitData();
	}

	@Test
	public void doesNotReceiveDataAfterClientRemoved() {
		rtpTestClient.getAudioAnalyser().awaitNoData();

		client = testManager.getMixingCore().addClient(
			CLIENT_ID, MixingCoreTestManager.MATRIX_HOST,
			ports.getClientToMatrix(), ports.getMatrixToClient());

		rtpTestClient.getAudioAnalyser().awaitData();

		mixingCore.removeClient(client);
		client = null;

		rtpTestClient.getAudioAnalyser().awaitNoData();
	}

	@Test
	public void receivesAudioDataAfterAddedAgain() {
		rtpTestClient.getAudioAnalyser().awaitNoData();

		client = testManager.getMixingCore().addClient(
			CLIENT_ID, MixingCoreTestManager.MATRIX_HOST,
			ports.getClientToMatrix(), ports.getMatrixToClient());

		rtpTestClient.getAudioAnalyser().awaitData();

		mixingCore.removeClient(client);
		client = null;

		rtpTestClient.getAudioAnalyser().awaitNoData();

		client = testManager.getMixingCore().addClient(
			CLIENT_ID, MixingCoreTestManager.MATRIX_HOST,
			ports.getClientToMatrix(), ports.getMatrixToClient());

		rtpTestClient.getAudioAnalyser().awaitData();
	}
}
