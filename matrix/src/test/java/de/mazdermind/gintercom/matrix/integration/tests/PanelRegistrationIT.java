package de.mazdermind.gintercom.matrix.integration.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.server.LocalServerPort;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.tools.ControlServerTestClient;
import de.mazdermind.gintercom.shared.controlserver.messages.ohai.Capabilities;
import de.mazdermind.gintercom.shared.controlserver.messages.ohai.OhaiMessage;
import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;

public class PanelRegistrationIT extends IntegrationTestBase {
	private static final String UNKNOWN_HOST_ID = "9999:9999";
	private static final String TEST_CLIENT_MODEL = "PanelRegistrationIT-client";
	private static final List<String> TEST_CLIENT_BUTTONS = ImmutableList.of("X1", "X2");

	private static final String HOST_ID = "0000-0001";
	private static final String PANEL_NAME = "Helpdesk 1";

	private OhaiMessage ohaiMessage;

	@LocalServerPort
	private int serverPort;

	private ControlServerTestClient client;

	@Before
	public void prepare() {
		client = new ControlServerTestClient(serverPort);
		ohaiMessage = new OhaiMessage()
			.setClientId(UNKNOWN_HOST_ID)
			.setClientModel(TEST_CLIENT_MODEL)
			.setProtocolVersion(1)
			.setCapabilities(new Capabilities()
				.setButtons(TEST_CLIENT_BUTTONS));
	}

	@After
	public void teardown() {
		client.assertNoOtherMessages();
		client.assertNoErrors();

		client.cleanup();
	}

	@Test
	public void panelRegistrationWithUnknownHostId() {
		client.connect();
		client.send("/ohai", ohaiMessage);

		assertThat(client.awaitMessages(), is(empty()));

		client.disconnect();
	}

	@Test
	public void panelRegistrationWithKnownHostIdRespondsWithExpectedProvisionMessage() {
		client.connect();

		ohaiMessage.setClientId(HOST_ID);
		client.send("/ohai", ohaiMessage);

		ProvisionMessage provisionMessage = client.awaitMessage("/user/provision", ProvisionMessage.class);
		assertThat(provisionMessage.getProvisioningInformation().getDisplay(), is(PANEL_NAME));

		client.disconnect();
	}
}
