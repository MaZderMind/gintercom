package de.mazdermind.gintercom.matrix.restapi.devices;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionInformation;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionManager;
import de.mazdermind.gintercom.matrix.integration.IntegrationTestBase;

public class DevicesServiceIT extends IntegrationTestBase {
	private static final String SESSION_ID = "THE_SESSION_ID";
	private static final String HOST_ID = "THE_HOST_ID";
	private static final String PANEL_ID = "THE_PANEL_ID";

	@Autowired
	private DevicesService devicesService;

	@Autowired
	private PanelConnectionManager panelConnectionManager;

	@After
	public void cleanup() {
		panelConnectionManager.deregisterPanelConnection(SESSION_ID);
	}

	@Test
	public void noDevicesOnline() {
		assertThat(devicesService.getOnlineDevices()).isEmpty();
		assertThat(devicesService.getProvisionedDevices()).isEmpty();
		assertThat(devicesService.getUnprovisionedDevices()).isEmpty();
	}

	@Test
	public void provisionedDeviceOnline() {
		PanelConnectionInformation connectionInformation = new PanelConnectionInformation()
			.setHostId(HOST_ID)
			.setSessionId(SESSION_ID);

		panelConnectionManager.registerPanelConnection(SESSION_ID, connectionInformation);

		assertThat(devicesService.getOnlineDevices()).hasSize(1)
			.extracting(DeviceDto::getHostId).contains(HOST_ID);

		assertThat(devicesService.getProvisionedDevices()).isEmpty();
		assertThat(devicesService.getUnprovisionedDevices()).hasSize(1);
	}

	@Test
	public void unprovisionedDeviceOnline() {
		PanelConnectionInformation connectionInformation = new PanelConnectionInformation()
			.setHostId(HOST_ID)
			.setPanelId(Optional.of(PANEL_ID))
			.setSessionId(SESSION_ID);

		panelConnectionManager.registerPanelConnection(SESSION_ID, connectionInformation);

		assertThat(devicesService.getOnlineDevices()).hasSize(1)
			.extracting(DeviceDto::getHostId).contains(HOST_ID);

		assertThat(devicesService.getProvisionedDevices()).hasSize(1);
		assertThat(devicesService.getUnprovisionedDevices()).isEmpty();
	}
}
