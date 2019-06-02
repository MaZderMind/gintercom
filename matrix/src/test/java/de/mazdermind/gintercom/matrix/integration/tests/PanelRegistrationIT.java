package de.mazdermind.gintercom.matrix.integration.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.integration.IntegrationWithoutGstreamerPipelineTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.matrix.integration.tools.controlserver.ControlServerTestClient;
import de.mazdermind.gintercom.shared.controlserver.messages.provision.AlreadyRegisteredMessage;
import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.Capabilities;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;

public class PanelRegistrationIT extends IntegrationWithoutGstreamerPipelineTestBase {
	private static final String PANEL_ID = "helpdesk";
	private static final String PANEL_NAME = "Helpdesk 1";
	private static final String HOST_ID = "0000:0000";

	private static final String TEST_CLIENT_MODEL = "PanelRegistrationIT-client";
	private static final List<String> TEST_CLIENT_BUTTONS = ImmutableList.of("X1", "X2");

	private PanelRegistrationMessage panelRegistrationMessage;

	@Autowired
	private ControlServerTestClient client;

	@Autowired
	private ControlServerTestClient client2;

	@Autowired
	private TestConfig testConfig;

	@Before
	public void prepare() {
		testConfig.reset();

		testConfig.getPanels()
			.put(PANEL_ID, new PanelConfig()
				.setDisplay(PANEL_NAME)
				.setHostId(HOST_ID));

		panelRegistrationMessage = new PanelRegistrationMessage()
			.setHostId(HOST_ID)
			.setClientModel(TEST_CLIENT_MODEL)
			.setProtocolVersion(1)
			.setCapabilities(new Capabilities()
				.setButtons(TEST_CLIENT_BUTTONS));
	}

	@After
	public void teardown() {
		client.assertNoOtherMessages();
		client2.assertNoOtherMessages();
		client.assertNoErrors();
		client2.assertNoErrors();

		client.cleanup();
		client2.cleanup();
	}

	@Test
	public void panelRegistrationWithUnknownHostId() {
		client.connect();
		client.send("/registration", panelRegistrationMessage);

		assertThat(client.awaitMessages(), is(empty()));

		client.disconnect();
	}

	@Test
	public void panelRegistrationWithKnownHostIdRespondsWithExpectedProvisionMessage() {
		client.connect();

		client.send("/registration", panelRegistrationMessage);

		ProvisionMessage provisionMessage = client.awaitMessage("/user/provision", ProvisionMessage.class);
		assertThat(provisionMessage.getProvisioningInformation().getDisplay(), is(PANEL_NAME));

		client.disconnect();
	}

	@Test
	public void panelCanReRegisterAfterDisconnect() {
		client.connect();
		client.send("/registration", panelRegistrationMessage);
		ProvisionMessage provisionMessage1 = client.awaitMessage("/user/provision", ProvisionMessage.class);
		assertThat(provisionMessage1.getProvisioningInformation().getDisplay(), is(PANEL_NAME));
		client.disconnect();

		client2.connect();
		client2.send("/registration", panelRegistrationMessage);
		ProvisionMessage provisionMessage2 = client2.awaitMessage("/user/provision", ProvisionMessage.class);
		assertThat(provisionMessage2.getProvisioningInformation().getDisplay(), is(PANEL_NAME));
		client2.disconnect();
	}

	@Test
	public void panelCanNotReRegisterWhileStillConnected() {
		client.connect();
		client.send("/registration", panelRegistrationMessage);
		ProvisionMessage provisionMessage = client.awaitMessage("/user/provision", ProvisionMessage.class);
		assertThat(provisionMessage.getProvisioningInformation().getDisplay(), is(PANEL_NAME));

		client2.connect();
		client2.send("/registration", panelRegistrationMessage);
		AlreadyRegisteredMessage alreadyRegisteredMessage = client2
			.awaitMessage("/user/provision/already-registered", AlreadyRegisteredMessage.class);
		assertThat(alreadyRegisteredMessage.getRemoteIp(), notNullValue());
		assertThat(alreadyRegisteredMessage.getConnectionTime(), notNullValue());
		client2.disconnect();

		client.disconnect();
	}
}
