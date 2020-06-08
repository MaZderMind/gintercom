package de.mazdermind.gintercom.matrix.restapi.devices;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.matrix.IntegrationTestBase;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import de.mazdermind.gintercom.matrix.tools.mocks.TestConfig;

public class DevicesServiceIT extends IntegrationTestBase {
	private static final InetSocketAddress SOCKET_ADDRESS = InetSocketAddress.createUnresolved("some-host", 32541);
	private static final String HOST_ID = "THE_HOST_ID";
	private static final String PANEL_ID = "THE_PANEL_ID";

	@Autowired
	private DevicesService devicesService;

	@Autowired
	private AssociatedClientsManager associatedClientsManager;

	@Autowired
	private TestConfig testConfig;

	@After
	public void cleanup() {
		associatedClientsManager.findAssociation(HOST_ID)
			.ifPresent(associatedClientsManager::deAssociate);
	}

	@Test
	public void noDevicesOnline() {
		assertThat(devicesService.getOnlineDevices()).isEmpty();
		assertThat(devicesService.getProvisionedDevices()).isEmpty();
		assertThat(devicesService.getUnprovisionedDevices()).isEmpty();
	}

	@Test
	public void unprovisionedDeviceOnline() {
		associatedClientsManager.associate(SOCKET_ADDRESS, HOST_ID);

		assertThat(devicesService.getOnlineDevices()).hasSize(1)
			.extracting(DeviceDto::getHostId).contains(HOST_ID);

		assertThat(devicesService.getProvisionedDevices()).isEmpty();
		assertThat(devicesService.getUnprovisionedDevices()).hasSize(1);
	}

	@Test
	public void provisionedDeviceOnline() {
		testConfig.getPanels().put(PANEL_ID, new PanelConfig().setHostId(HOST_ID));
		associatedClientsManager.associate(SOCKET_ADDRESS, HOST_ID);

		assertThat(devicesService.getOnlineDevices()).hasSize(1)
			.extracting(DeviceDto::getHostId).contains(HOST_ID);

		assertThat(devicesService.getProvisionedDevices()).hasSize(1);
		assertThat(devicesService.getUnprovisionedDevices()).isEmpty();
	}
}
