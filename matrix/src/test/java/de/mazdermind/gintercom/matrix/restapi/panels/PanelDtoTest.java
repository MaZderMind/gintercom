package de.mazdermind.gintercom.matrix.restapi.panels;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PanelDtoTest {
	private static final String HOST_ID = "THE_HOST_ID";

	@Test
	public void provisionedState() {
		PanelDto dto = new PanelDto();
		assertThat(dto.getHostId()).isNull();
		assertThat(dto.isAssigned()).isFalse();

		dto.setHostId(HOST_ID);
		assertThat(dto.getHostId()).isNotNull();
		assertThat(dto.isAssigned()).isTrue();

		dto.setHostId(null);
		assertThat(dto.getHostId()).isNull();
		assertThat(dto.isAssigned()).isFalse();
	}

}
