package de.mazdermind.gintercom.matrix.integration.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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

public class TwoPanelsRegistrationIT extends IntegrationTestBase {
	private static final String HOST_ID_1 = "0000-0001";
	private static final String HOST_ID_2 = "0000-0002";

	private static final String PANEL_NAME1 = "Helpdesk 1";
	private static final String PANEL_NAME2 = "Helpdesk 2";

	private static final String TEST_CLIENT_MODEL = "TwoPanelsRegistrationIT-client";
	private static final List<String> TEST_CLIENT_BUTTONS = ImmutableList.of("X1", "X2");

	private OhaiMessage ohaiMessage1;
	private OhaiMessage ohaiMessage2;

	@LocalServerPort
	private int serverPort;

	private ControlServerTestClient client1;
	private ControlServerTestClient client2;

	@Before
	public void prepare() {
		client1 = new ControlServerTestClient(serverPort);
		client2 = new ControlServerTestClient(serverPort);

		ohaiMessage1 = new OhaiMessage()
			.setClientId(HOST_ID_1)
			.setClientModel(TEST_CLIENT_MODEL)
			.setProtocolVersion(1)
			.setCapabilities(new Capabilities()
				.setButtons(TEST_CLIENT_BUTTONS));

		ohaiMessage2 = new OhaiMessage()
			.setClientId(HOST_ID_2)
			.setClientModel(TEST_CLIENT_MODEL)
			.setProtocolVersion(1)
			.setCapabilities(new Capabilities()
				.setButtons(TEST_CLIENT_BUTTONS));
	}

	@After
	public void teardown() {
		client1.assertNoOtherMessages();
		client2.assertNoOtherMessages();
		client1.assertNoErrors();
		client2.assertNoErrors();

		client1.cleanup();
		client2.cleanup();
	}

	@Test
	public void twoPanelsRegistering() {
		client1.connect();
		client2.connect();

		client1.send("/ohai", ohaiMessage1);
		client2.send("/ohai", ohaiMessage2);

		ProvisionMessage provisionMessage1 = client1.awaitMessage("/user/provision", ProvisionMessage.class);
		ProvisionMessage provisionMessage2 = client2.awaitMessage("/user/provision", ProvisionMessage.class);

		assertThat(provisionMessage1.getProvisioningInformation().getDisplay(), is(PANEL_NAME1));
		assertThat(provisionMessage2.getProvisioningInformation().getDisplay(), is(PANEL_NAME2));

		client1.disconnect();
		client2.disconnect();
	}
}
