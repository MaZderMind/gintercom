package de.mazdermind.gintercom.matrix.restapi.clients;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ClientDtoTest {
	private static final String PANEL_ID = "THE_PANEL_ID";

	@Test
	public void provisionedState() {
		ClientDto dto = new ClientDto();
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
