package de.mazdermind.gintercom.matrix.integration.tests.registration;

import static de.mazdermind.gintercom.matrix.integration.tools.builder.RandomPanelRegistrationMessageBuilder.randomPanelRegistrationMessageForPanelConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.matrix.integration.tools.controlserver.ControlServerTestClient;
import de.mazdermind.gintercom.shared.controlserver.messages.provision.AlreadyRegisteredMessage;
import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;

public class PanelRegistrationIT extends IntegrationTestBase {

	@Autowired
	private ControlServerTestClient client1;

	@Autowired
	private ControlServerTestClient client2;

	@Autowired
	private TestConfig testConfig;

	private PanelRegistrationMessage panelRegistrationMessage;
	private PanelConfig panelConfig;

	@Before
	public void prepare() {
		testConfig.reset();

		panelConfig = testConfig.addRandomPanel();
		panelRegistrationMessage = randomPanelRegistrationMessageForPanelConfig(panelConfig);
	}

	@After
	public void teardown() {
		client1.cleanup();
		client2.cleanup();
	}

	@Test
	public void panelRegistrationWithUnknownHostId() {
		client1.connect();
		client1.send("/registration", panelRegistrationMessage);

		assertThat(client1.awaitMessages(), is(empty()));

		client1.disconnect();
	}

	@Test
	public void panelRegistrationWithKnownHostIdRespondsWithExpectedProvisionMessage() {
		client1.connect();

		client1.send("/registration", panelRegistrationMessage);

		ProvisionMessage provisionMessage = client1.awaitMessage("/user/provision", ProvisionMessage.class);
		assertThat(provisionMessage.getProvisioningInformation().getDisplay(), is(panelConfig.getDisplay()));

		client1.disconnect();
	}

	@Test
	public void panelCanReRegisterAfterDisconnect() {
		client1.connect();
		client1.send("/registration", panelRegistrationMessage);
		ProvisionMessage provisionMessage1 = client1.awaitMessage("/user/provision", ProvisionMessage.class);
		assertThat(provisionMessage1.getProvisioningInformation().getDisplay(), is(panelConfig.getDisplay()));
		client1.disconnect();

		client2.connect();
		client2.send("/registration", panelRegistrationMessage);
		ProvisionMessage provisionMessage2 = client2.awaitMessage("/user/provision", ProvisionMessage.class);
		assertThat(provisionMessage2.getProvisioningInformation().getDisplay(), is(panelConfig.getDisplay()));

		assertThat(provisionMessage2.getProvisioningInformation().getPanelToMatrixPort(),
			is(provisionMessage1.getProvisioningInformation().getPanelToMatrixPort()));

		assertThat(provisionMessage2.getProvisioningInformation().getPanelToMatrixPort(),
			is(provisionMessage1.getProvisioningInformation().getPanelToMatrixPort()));

		client2.disconnect();
	}

	@Test
	public void panelCanNotReRegisterWhileStillConnected() {
		client1.connect();
		client1.send("/registration", panelRegistrationMessage);
		ProvisionMessage provisionMessage = client1.awaitMessage("/user/provision", ProvisionMessage.class);
		assertThat(provisionMessage.getProvisioningInformation().getDisplay(), is(panelConfig.getDisplay()));

		client2.connect();
		client2.send("/registration", panelRegistrationMessage);
		AlreadyRegisteredMessage alreadyRegisteredMessage = client2
			.awaitMessage("/user/provision/already-registered", AlreadyRegisteredMessage.class);
		assertThat(alreadyRegisteredMessage.getRemoteIp(), notNullValue());
		assertThat(alreadyRegisteredMessage.getConnectionTime(), notNullValue());
		client2.disconnect();

		client1.disconnect();
	}
}
