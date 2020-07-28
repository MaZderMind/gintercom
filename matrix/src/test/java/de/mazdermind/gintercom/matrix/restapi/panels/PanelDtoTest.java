package de.mazdermind.gintercom.matrix.restapi.panels;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;

public class PanelDtoTest {

	private String clientId;

	@Before
	public void before() {
		clientId = TestClientIdGenerator.generateTestClientId();
	}

	@Test
	public void provisionedState() {
		PanelDto dto = new PanelDto();
		assertThat(dto.getClientId()).isNull();
		assertThat(dto.isAssigned()).isFalse();

		dto.setClientId(clientId);
		assertThat(dto.getClientId()).isNotNull();
		assertThat(dto.isAssigned()).isTrue();

		dto.setClientId(null);
		assertThat(dto.getClientId()).isNull();
		assertThat(dto.isAssigned()).isFalse();
	}

}
