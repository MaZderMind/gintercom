package de.mazdermind.gintercom.matrix.restapi.panels;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;

public class PanelsServiceIT extends IntegrationTestBase {
	private static final InetSocketAddress SOCKET_ADDRESS = InetSocketAddress.createUnresolved("some-host", 32541);
	private static final String HOST_ID = "THE_HOST_ID";
	private static final String PANEL_ID = "THE_PANEL_ID";

	@Autowired
	private PanelsService panelsService;

	@Autowired
	private TestConfig testConfig;

	@Autowired
	private AssociatedClientsManager associatedClientsManager;

	@After
	public void cleanup() {
		associatedClientsManager.findAssociation(HOST_ID)
			.ifPresent(associatedClientsManager::deAssociate);
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
		testConfig.getPanels().put(PANEL_ID, new PanelConfig());

		assertThat(panelsService.getConfiguredPanels()).hasSize(1)
			.extracting(PanelDto::getId).contains(PANEL_ID);

		assertThat(panelsService.getAssignedPanels()).isEmpty();
		assertThat(panelsService.getUnassignedPanels()).hasSize(1);
		assertThat(panelsService.getOnlinePanels()).isEmpty();
		assertThat(panelsService.getOfflinePanels()).hasSize(1);
	}

	@Test
	public void assignedPanel() {
		testConfig.getPanels().put(PANEL_ID, new PanelConfig().setHostId(HOST_ID));

		assertThat(panelsService.getConfiguredPanels()).hasSize(1)
			.extracting(PanelDto::getId).contains(PANEL_ID);

		assertThat(panelsService.getAssignedPanels()).hasSize(1);
		assertThat(panelsService.getUnassignedPanels()).isEmpty();
		assertThat(panelsService.getOnlinePanels()).isEmpty();
		assertThat(panelsService.getOfflinePanels()).hasSize(1);
	}

	@Test
	public void onlinePanel() {
		testConfig.getPanels().put(PANEL_ID, new PanelConfig().setHostId(HOST_ID));
		associatedClientsManager.associate(SOCKET_ADDRESS, HOST_ID);

		assertThat(panelsService.getConfiguredPanels()).hasSize(1)
			.extracting(PanelDto::getId).contains(PANEL_ID);

		assertThat(panelsService.getAssignedPanels()).hasSize(1);
		assertThat(panelsService.getUnassignedPanels()).isEmpty();
		assertThat(panelsService.getOnlinePanels()).hasSize(1);
		assertThat(panelsService.getOfflinePanels()).isEmpty();
	}
}
