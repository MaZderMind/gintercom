package de.mazdermind.gintercom.matrix.restapi.devices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionInformation;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionManager;

@RunWith(MockitoJUnitRunner.class)
public class DevicesServiceTest {
	@InjectMocks
	private DevicesService devicesService;

	@Mock
	private PanelConnectionManager panelConnectionManager;

	@Before
	public void configureMocks() {
		when(panelConnectionManager.getConnectedPanels()).thenReturn(ImmutableList.of(
			new PanelConnectionInformation()
				.setHostId("10:00")
				.setConnectionTime(LocalDateTime.of(2020, 5, 1, 10, 0)),

			new PanelConnectionInformation()
				.setHostId("12:00")
				.setConnectionTime(LocalDateTime.of(2020, 5, 1, 12, 0)),

			new PanelConnectionInformation()
				.setHostId("11:00")
				.setConnectionTime(LocalDateTime.of(2020, 5, 1, 11, 0))
		));
	}

	@Test
	public void sortsNewerDevicesFirst() {
		assertThat(devicesService.getOnlineDevices())
			.extracting(DeviceDto::getHostId)
			.containsExactly("12:00", "11:00", "10:00");
	}
}
