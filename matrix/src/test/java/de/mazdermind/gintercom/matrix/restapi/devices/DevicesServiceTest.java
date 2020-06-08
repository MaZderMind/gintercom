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

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;

@RunWith(MockitoJUnitRunner.class)
public class DevicesServiceTest {
	@InjectMocks
	private DevicesService devicesService;

	@Mock
	private AssociatedClientsManager associatedClientsManager;

	@Mock
	private Config config;

	@Before
	public void configureMocks() {
		when(associatedClientsManager.getAssociations()).thenReturn(ImmutableList.of(
			new ClientAssociation()
				.setHostId("10:00")
				.setFirstSeen(LocalDateTime.of(2020, 5, 1, 10, 0)),

			new ClientAssociation()
				.setHostId("12:00")
				.setFirstSeen(LocalDateTime.of(2020, 5, 1, 12, 0)),

			new ClientAssociation()
				.setHostId("11:00")
				.setFirstSeen(LocalDateTime.of(2020, 5, 1, 11, 0))
		));
	}

	@Test
	public void sortsNewerDevicesFirst() {
		assertThat(devicesService.getOnlineDevices())
			.extracting(DeviceDto::getHostId)
			.containsExactly("12:00", "11:00", "10:00");
	}
}
