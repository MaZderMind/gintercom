package de.mazdermind.gintercom.matrix.restapi.devices;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DeviceDtoTest {
	private static final String PANEL_ID = "THE_PANEL_ID";

	@Test
	public void provisionedState() {
		DeviceDto dto = new DeviceDto();
		assertThat(dto.getPanelId()).isNull();
		assertThat(dto.isProvisioned()).isFalse();

		dto.setPanelId(PANEL_ID);
		assertThat(dto.getPanelId()).isNotNull();
		assertThat(dto.isProvisioned()).isTrue();

		dto.setPanelId(null);
		assertThat(dto.getPanelId()).isNull();
		assertThat(dto.isProvisioned()).isFalse();
	}
}
