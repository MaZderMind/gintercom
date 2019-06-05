package de.mazdermind.gintercom.matrix.integration.tests.registration;

import static de.mazdermind.gintercom.matrix.integration.tools.builder.RandomPanelRegistrationMessageBuilder.randomPanelRegistrationMessageForPanelConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.matrix.integration.tools.controlserver.ControlServerTestClient;
import de.mazdermind.gintercom.matrix.integration.tools.rtp.RtpTestClient;
import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;

public class RegistrationAudioTransmissionIT extends IntegrationTestBase {
	private static Logger log = LoggerFactory.getLogger(RegistrationAudioTransmissionIT.class);

	@Autowired
	private RtpTestClient rtpClient;

	@Autowired
	private ControlServerTestClient client;

	@Autowired
	private TestConfig testConfig;

	private PanelConfig panelConfig;
	private PanelRegistrationMessage panelRegistrationMessage;

	@Before
	public void prepare() {
		testConfig.reset();

		panelConfig = testConfig.addRandomPanel();
		panelRegistrationMessage = randomPanelRegistrationMessageForPanelConfig(panelConfig);
	}

	@After
	public void teardown() {
		client.cleanup();
		rtpClient.cleanup();
	}

	@Test
	public void receivesAudioDataBetweenRegisterAndDisconnect() {
		client.connect();
		client.send("/registration", panelRegistrationMessage);

		ProvisionMessage provisionMessage = client.awaitMessage("/user/provision", ProvisionMessage.class);
		rtpClient.connect(provisionMessage.getProvisioningInformation());

		rtpClient.getRx().awaitAudioData();

		client.disconnect();

		rtpClient.getRx().awaitNoAudioData();
	}

	@Test
	public void receivesAudioDataAfterReRegistering() {
		log.info("Connection 1");
		client.connect();
		client.send("/registration", panelRegistrationMessage);
		ProvisionMessage provisionMessage1 = client.awaitMessage("/user/provision", ProvisionMessage.class);

		rtpClient.connect(provisionMessage1.getProvisioningInformation());

		rtpClient.getRx().awaitAudioData();

		log.info("Disconnect 1");
		client.disconnect();
		rtpClient.getRx().awaitNoAudioData();
		log.info("Done");

		log.info("Connection 2");
		client.connect();
		client.send("/registration", panelRegistrationMessage);
		ProvisionMessage provisionMessage2 = client.awaitMessage("/user/provision", ProvisionMessage.class);

		rtpClient.getRx().awaitAudioData();

		log.info("Disconnect 2");
		client.disconnect();
		rtpClient.getRx().awaitNoAudioData();
		log.info("Done");
	}
}
