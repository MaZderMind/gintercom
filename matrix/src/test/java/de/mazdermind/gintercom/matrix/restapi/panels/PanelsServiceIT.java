package de.mazdermind.gintercom.matrix.restapi.panels;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;

public class PanelsServiceIT extends IntegrationTestBase {
	private static final InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress("10.0.0.1", 32541);
	private static final String CLIENT_MODEL = "THE_CLIENT_MODEL";

	@Autowired
	private PanelsService panelsService;

	@Autowired
	private TestConfig testConfig;

	@Autowired
	private AssociatedClientsManager associatedClientsManager;

	private String clientId;

	@Before
	public void before() {
		clientId = TestClientIdGenerator.generateTestClientId();
	}

	@After
	public void cleanup() {
		associatedClientsManager.findAssociation(clientId)
			.ifPresent(clientAssociation -> associatedClientsManager.deAssociate(clientAssociation, "Test"));
	}

	@Test
	public void noPanels() {
		assertThat(panelsService.getConfiguredPanels()).isEmpty();
		assertThat(panelsService.getAssignedPanels()).isEmpty();
		assertThat(panelsService.getUnassignedPanels()).isEmpty();
		assertThat(panelsService.getOnlinePanels()).isEmpty();
		assertThat(panelsService.getOfflinePanels()).isEmpty();
	}

	@Test
	public void unassignedPanel() {
		String panelId = testConfig.addRandomPanel();
		PanelConfig panelConfig = testConfig.getPanels().get(panelId);

		assertThat(panelsService.getConfiguredPanels()).hasSize(1)
			.extracting(PanelInfoDto::getId).contains(panelId);

		assertThat(panelsService.getAssignedPanels()).isEmpty();
		assertThat(panelsService.getUnassignedPanels()).hasSize(1);
		assertThat(panelsService.getOnlinePanels()).isEmpty();
		assertThat(panelsService.getOfflinePanels()).hasSize(1);

		PanelInfoDto panel = panelsService.getConfiguredPanels().findFirst().orElseThrow(AssertionError::new);
		assertThat(panel.getId()).isEqualTo(panelId);
		assertThat(panel.getDisplay()).isEqualTo(panelConfig.getDisplay());
		assertThat(panel.getClientId()).isEqualTo(null);
		assertThat(panel.getClientModel()).isEqualTo(null);
	}

	@Test
	public void assignedPanel() {
		String panelId = testConfig.addRandomPanel(clientId);
		PanelConfig panelConfig = testConfig.getPanels().get(panelId);

		assertThat(panelsService.getConfiguredPanels()).hasSize(1)
			.flatExtracting(PanelInfoDto::getId).contains(panelId);

		assertThat(panelsService.getAssignedPanels()).hasSize(1);
		assertThat(panelsService.getUnassignedPanels()).isEmpty();
		assertThat(panelsService.getOnlinePanels()).isEmpty();
		assertThat(panelsService.getOfflinePanels()).hasSize(1);

		PanelInfoDto panel = panelsService.getConfiguredPanels().findFirst().orElseThrow(AssertionError::new);
		assertThat(panel.getId()).isEqualTo(panelId);
		assertThat(panel.getDisplay()).isEqualTo(panelConfig.getDisplay());
		assertThat(panel.getClientId()).isEqualTo(clientId);
		assertThat(panel.getClientModel()).isEqualTo(null);
	}

	@Test
	public void onlinePanel() {
		String panelId = testConfig.addRandomPanel(clientId);
		PanelConfig panelConfig = testConfig.getPanels().get(panelId);
		associatedClientsManager.associate(SOCKET_ADDRESS, clientId, CLIENT_MODEL);

		assertThat(panelsService.getConfiguredPanels()).hasSize(1)
			.extracting(PanelInfoDto::getId).contains(panelId);

		assertThat(panelsService.getAssignedPanels()).hasSize(1);
		assertThat(panelsService.getUnassignedPanels()).isEmpty();
		assertThat(panelsService.getOnlinePanels()).hasSize(1);
		assertThat(panelsService.getOfflinePanels()).isEmpty();

		PanelInfoDto panel = panelsService.getConfiguredPanels().findFirst().orElseThrow(AssertionError::new);
		assertThat(panel.getId()).isEqualTo(panelId);
		assertThat(panel.getDisplay()).isEqualTo(panelConfig.getDisplay());
		assertThat(panel.getClientId()).isEqualTo(clientId);
		assertThat(panel.getClientModel()).isEqualTo(CLIENT_MODEL);
	}
}
