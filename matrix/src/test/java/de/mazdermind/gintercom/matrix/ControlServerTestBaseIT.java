package de.mazdermind.gintercom.matrix;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;

public class ControlServerTestBaseIT extends ControlServerTestBase {
	private static final String PANEL_ID = "THE_PANEL_ID";

	@Autowired
	private TestConfig testConfig;

	@Test
	public void testAssociationHelper() {
		assertThat(associatedClientsManager.getAssociations()).isEmpty();
		ClientAssociation association = associateClient();
		assertThat(associatedClientsManager.getAssociations()).hasSize(1);

		eventReceiver.assertNoMoreEvents();
		client.assertNoMoreMessages();

		assertThat(associatedClientsManager.getAssociation(HOST_ID)).isEqualTo(association);
		deAssociateClient();
		assertThat(associatedClientsManager.getAssociations()).isEmpty();

		eventReceiver.assertNoMoreEvents();
		client.assertNoMoreMessages();
	}

	@Test
	public void testAssociationHelperWithConfiguredPanel() {
		PanelConfig panelConfig = new PanelConfig()
			.setHostId(HOST_ID)
			.setDisplay("THE_DISPLAY_NAME");

		testConfig.getPanels().put(PANEL_ID, panelConfig);

		assertThat(associatedClientsManager.getAssociations()).isEmpty();
		ClientAssociation association = associateClient();
		assertThat(associatedClientsManager.getAssociations()).hasSize(1);
		eventReceiver.assertNoMoreEvents();
		client.assertNoMoreMessages();

		assertThat(associatedClientsManager.getAssociation(HOST_ID)).isEqualTo(association);
		deAssociateClient();
		assertThat(associatedClientsManager.getAssociations()).isEmpty();

		eventReceiver.assertNoMoreEvents();
		client.assertNoMoreMessages();
	}
}
