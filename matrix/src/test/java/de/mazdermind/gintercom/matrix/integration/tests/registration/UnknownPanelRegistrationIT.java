package de.mazdermind.gintercom.matrix.integration.tests.registration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.matrix.integration.IntegrationWithoutGstreamerPipelineTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;
import de.mazdermind.gintercom.matrix.integration.tools.controlserver.ControlServerTestClient;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.Capabilities;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;

public class UnknownPanelRegistrationIT extends IntegrationWithoutGstreamerPipelineTestBase {
	private static final String HOST_ID = "0000:0000";

	private static final String TEST_CLIENT_MODEL = "UnknownPanelRegistrationIT-client";
	private static final List<String> TEST_CLIENT_BUTTONS = ImmutableList.of("X1", "X2");

	private PanelRegistrationMessage panelRegistrationMessage;

	@Autowired
	private ControlServerTestClient client;

	@Autowired
	private TestConfig testConfig;

	@Before
	public void prepare() {
		testConfig.reset();

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
		client.assertNoErrors();

		client.cleanup();
	}

	@Test
	public void panelRegistrationWithUnknownHostId() {
		client.connect();
		client.send("/registration", panelRegistrationMessage);

		assertThat(client.awaitMessages(), is(empty()));

		client.disconnect();
	}
}
