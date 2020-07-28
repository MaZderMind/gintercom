package de.mazdermind.gintercom.matrix;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;

public class ControlServerTestBaseIT extends ControlServerTestBase {
	@Autowired
	private TestConfig testConfig;

	private String clientId;

	@Before
	public void before() {
		clientId = TestClientIdGenerator.generateTestClientId();
	}

	@Test
	public void testAssociationHelper() {
		assertThat(associatedClientsManager.getAssociations()).isEmpty();
		ClientAssociation association = associateClient(clientId);
		assertThat(associatedClientsManager.getAssociations()).hasSize(1);

		eventReceiver.assertNoMoreEvents();
		client.assertNoMoreMessages();

		assertThat(associatedClientsManager.getAssociation(clientId)).isEqualTo(association);
		deAssociateClient();
		assertThat(associatedClientsManager.getAssociations()).isEmpty();

		eventReceiver.assertNoMoreEvents();
		client.assertNoMoreMessages();
	}

	@Test
	public void testAssociationHelperWithConfiguredPanel() {
		testConfig.addRandomPanel(clientId);

		assertThat(associatedClientsManager.getAssociations()).isEmpty();
		ClientAssociation association = associateClient(clientId);
		assertThat(associatedClientsManager.getAssociations()).hasSize(1);
		eventReceiver.assertNoMoreEvents();
		client.assertNoMoreMessages();

		assertThat(associatedClientsManager.getAssociation(clientId)).isEqualTo(association);
		deAssociateClient();
		assertThat(associatedClientsManager.getAssociations()).isEmpty();

		eventReceiver.assertNoMoreEvents();
		client.assertNoMoreMessages();
	}
}
