package de.mazdermind.gintercom.matrix.restapi.panels;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PanelDtoTest {
	private static final String CLIENT_ID = "THE_CLIENT_ID";

	@Test
	public void provisionedState() {
		PanelDto dto = new PanelDto();
		assertThat(dto.getClientId()).isNull();
		assertThat(dto.isAssigned()).isFalse();

		dto.setClientId(CLIENT_ID);
		assertThat(dto.getClientId()).isNotNull();
		assertThat(dto.isAssigned()).isTrue();

		dto.setClientId(null);
		assertThat(dto.getClientId()).isNull();
		assertThat(dto.isAssigned()).isFalse();
	}

}
