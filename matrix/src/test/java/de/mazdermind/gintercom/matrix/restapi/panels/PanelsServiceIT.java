package de.mazdermind.gintercom.matrix.restapi.panels;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionInformation;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionManager;
import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.integration.TestConfig;

public class PanelsServiceIT extends IntegrationTestBase {
	private static final String SESSION_ID = "THE_SESSION_ID";
	private static final String HOST_ID = "THE_HOST_ID";
	private static final String PANEL_ID = "THE_PANEL_ID";

	@Autowired
	private PanelsService panelsService;

	@Autowired
	private TestConfig testConfig;

	@Autowired
	private PanelConnectionManager panelConnectionManager;

	@Before
	public void resetConfig() {
		testConfig.reset();
	}

	@After
	public void cleanup() {
		panelConnectionManager.deregisterPanelConnection(SESSION_ID);
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
		PanelConnectionInformation connectionInformation = new PanelConnectionInformation()
			.setHostId(HOST_ID)
			.setPanelId(Optional.of(PANEL_ID))
			.setSessionId(SESSION_ID);

		panelConnectionManager.registerPanelConnection(SESSION_ID, connectionInformation);
		testConfig.getPanels().put(PANEL_ID, new PanelConfig().setHostId(HOST_ID));

		assertThat(panelsService.getConfiguredPanels()).hasSize(1)
			.extracting(PanelDto::getId).contains(PANEL_ID);

		assertThat(panelsService.getAssignedPanels()).hasSize(1);
		assertThat(panelsService.getUnassignedPanels()).isEmpty();
		assertThat(panelsService.getOnlinePanels()).hasSize(1);
		assertThat(panelsService.getOfflinePanels()).isEmpty();
	}
}
