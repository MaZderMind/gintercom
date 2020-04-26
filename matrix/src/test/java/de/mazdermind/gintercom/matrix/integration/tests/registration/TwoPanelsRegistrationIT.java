package de.mazdermind.gintercom.matrix.integration.tests.registration;

import static de.mazdermind.gintercom.matrix.integration.tools.builder.RandomPanelRegistrationMessageBuilder.randomPanelRegistrationMessageForPanelConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.matrix.integration.tools.controlserver.ControlServerTestClient;
import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;

public class TwoPanelsRegistrationIT extends IntegrationTestBase {
	@Autowired
	private ControlServerTestClient client1;

	@Autowired
	private ControlServerTestClient client2;

	@Autowired
	private TestConfig testConfig;

	private PanelRegistrationMessage panelRegistrationMessage1;
	private PanelRegistrationMessage panelRegistrationMessage2;

	private PanelConfig panelConfig1;
	private PanelConfig panelConfig2;

	@Before
	public void prepare() {
		testConfig.reset();
		panelConfig1 = testConfig.addRandomPanel();
		panelConfig2 = testConfig.addRandomPanel();

		panelRegistrationMessage1 = randomPanelRegistrationMessageForPanelConfig(panelConfig1);
		panelRegistrationMessage2 = randomPanelRegistrationMessageForPanelConfig(panelConfig2);
	}

	@After
	public void teardown() {
		client1.cleanup();
		client2.cleanup();
	}

	@Test
	public void twoDifferentPanelsCanRegisterSimultaniously() {
		client1.connect();
		client2.connect();

		client1.send("/registration", panelRegistrationMessage1);
		client2.send("/registration", panelRegistrationMessage2);

		ProvisionMessage provisionMessage1 = client1.awaitMessage("/user/provision", ProvisionMessage.class);
		ProvisionMessage provisionMessage2 = client2.awaitMessage("/user/provision", ProvisionMessage.class);

		assertThat(provisionMessage1.getProvisioningInformation().getDisplay(), is(panelConfig1.getDisplay()));
		assertThat(provisionMessage2.getProvisioningInformation().getDisplay(), is(panelConfig2.getDisplay()));

		client1.disconnect();
		client2.disconnect();
	}
}
