package de.mazdermind.gintercom.matrix.integration.tests.registration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.Capabilities;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;

public class TwoPanelsRegistrationIT extends IntegrationWithoutGstreamerPipelineTestBase {
	private static final String HOST_ID_1 = "0000-0001";
	private static final String HOST_ID_2 = "0000-0002";

	private static final String PANEL_NAME1 = "Helpdesk 1";
	private static final String PANEL_NAME2 = "Helpdesk 2";

	private static final String PANEL_ID1 = "helpdesk1";
	private static final String PANEL_ID2 = "helpdesk2";

	private static final String TEST_CLIENT_MODEL = "TwoPanelsRegistrationIT-client";
	private static final List<String> TEST_CLIENT_BUTTONS = ImmutableList.of("X1", "X2");

	private PanelRegistrationMessage panelRegistrationMessage1;
	private PanelRegistrationMessage panelRegistrationMessage2;

	@Autowired
	private ControlServerTestClient client1;

	@Autowired
	private ControlServerTestClient client2;

	@Autowired
	private TestConfig testConfig;

	@Before
	public void prepare() {
		testConfig.reset();
		testConfig.getPanels()
			.put(PANEL_ID1, new PanelConfig()
				.setHostId(HOST_ID_1)
				.setDisplay(PANEL_NAME1));

		testConfig.getPanels()
			.put(PANEL_ID2, new PanelConfig()
				.setHostId(HOST_ID_2)
				.setDisplay(PANEL_NAME2));

		panelRegistrationMessage1 = new PanelRegistrationMessage()
			.setHostId(HOST_ID_1)
			.setClientModel(TEST_CLIENT_MODEL)
			.setProtocolVersion(1)
			.setCapabilities(new Capabilities()
				.setButtons(TEST_CLIENT_BUTTONS));

		panelRegistrationMessage2 = new PanelRegistrationMessage()
			.setHostId(HOST_ID_2)
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

		client1.send("/registration", panelRegistrationMessage1);
		client2.send("/registration", panelRegistrationMessage2);

		ProvisionMessage provisionMessage1 = client1.awaitMessage("/user/provision", ProvisionMessage.class);
		ProvisionMessage provisionMessage2 = client2.awaitMessage("/user/provision", ProvisionMessage.class);

		assertThat(provisionMessage1.getProvisioningInformation().getDisplay(), is(PANEL_NAME1));
		assertThat(provisionMessage2.getProvisioningInformation().getDisplay(), is(PANEL_NAME2));

		client1.disconnect();
		client2.disconnect();
	}
}
