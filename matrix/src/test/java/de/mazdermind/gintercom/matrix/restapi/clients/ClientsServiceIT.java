package de.mazdermind.gintercom.matrix.restapi.clients;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;

public class ClientsServiceIT extends IntegrationTestBase {
	private static final InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress("10.0.0.1", 32541);
	private static final String CLIENT_MODEL = "THE_CLIENT_MODEL";

	@Autowired
	private ClientsService clientsService;

	@Autowired
	private AssociatedClientsManager associatedClientsManager;

	@Autowired
	private TestConfig testConfig;

	private String clientId;

	@Before
	public void before() {
		clientId = TestClientIdGenerator.generateTestClientId();
	}

	@After
	public void cleanup() {
		associatedClientsManager.findAssociation(clientId)
			.ifPresent(clientAssociation -> associatedClientsManager.deAssociate(clientAssociation, "Test Cleanup"));
	}

	@Test
	public void noClientsOnline() {
		assertThat(clientsService.getOnlineClients()).isEmpty();
		assertThat(clientsService.getProvisionedClients()).isEmpty();
		assertThat(clientsService.getUnprovisionedClients()).isEmpty();
	}

	@Test
	public void unprovisionedClientOnline() {
		associatedClientsManager.associate(SOCKET_ADDRESS, clientId, CLIENT_MODEL);

		assertThat(clientsService.getOnlineClients()).hasSize(1)
			.flatExtracting(ClientDto::getClientId, ClientDto::getClientModel)
			.contains(clientId, CLIENT_MODEL);

		assertThat(clientsService.getProvisionedClients()).isEmpty();
		assertThat(clientsService.getUnprovisionedClients()).hasSize(1);
	}

	@Test
	public void provisionedClientOnline() {
		testConfig.addRandomPanel(clientId);
		associatedClientsManager.associate(SOCKET_ADDRESS, clientId, CLIENT_MODEL);

		assertThat(clientsService.getOnlineClients()).hasSize(1)
			.flatExtracting(ClientDto::getClientId, ClientDto::getClientModel)
			.contains(clientId, CLIENT_MODEL);

		assertThat(clientsService.getProvisionedClients()).hasSize(1);
		assertThat(clientsService.getUnprovisionedClients()).isEmpty();
	}
}
