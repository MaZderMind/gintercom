package de.mazdermind.gintercom.matrix.integration.tests.registration;

import static de.mazdermind.gintercom.matrix.integration.tools.builder.RandomPanelRegistrationMessageBuilder.randomPanelRegistrationMessage;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.matrix.integration.tools.controlserver.ControlServerTestClient;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;

public class UnknownPanelRegistrationIT extends IntegrationTestBase {
	@Autowired
	private ControlServerTestClient client;

	@Autowired
	private TestConfig testConfig;

	private PanelRegistrationMessage panelRegistrationMessage;

	@Before
	public void prepare() {
		testConfig.reset();
		panelRegistrationMessage = randomPanelRegistrationMessage();
	}

	@After
	public void teardown() {
		client.cleanup();
	}

	@Test
	public void panelRegistrationWithUnknownHostId() {
		client.connect();
		client.send("/registration", panelRegistrationMessage);

		assertThat(client.awaitMessages()).isEmpty();

		client.disconnect();
	}
}
