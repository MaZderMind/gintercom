package de.mazdermind.gintercom.matrix.restapi.clients;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.oblac.nomen.Nomen;

public class ClientDtoTest {

	private String panelId;

	@Before
	public void before() {
		panelId = Nomen.randomName();
	}

	@Test
	public void provisionedState() {
		ClientDto dto = new ClientDto();
		assertThat(dto.getPanelId()).isNull();
		assertThat(dto.isProvisioned()).isFalse();

		dto.setPanelId(panelId);
		assertThat(dto.getPanelId()).isNotNull();
		assertThat(dto.isProvisioned()).isTrue();

		dto.setPanelId(null);
		assertThat(dto.getPanelId()).isNull();
		assertThat(dto.isProvisioned()).isFalse();
	}
}
