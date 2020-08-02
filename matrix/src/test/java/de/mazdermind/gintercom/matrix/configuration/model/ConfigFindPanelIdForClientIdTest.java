package de.mazdermind.gintercom.matrix.configuration.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ConfigFindPanelIdForClientIdTest {

	private Config config;

	@Before
	public void before() {
		config = new Config()
			.setPanels(ImmutableMap.of(
				"p1", new PanelConfig().setClientId("TEST-0001"),
				"p2", new PanelConfig().setClientId("TEST-0002"),
				"pZ", new PanelConfig()
			));
	}

	@Test
	public void canGetPanelConfigByClientId() {
		assertThat(config.findPanelIdForClientId("TEST-0000")).isEmpty();
		assertThat(config.findPanelIdForClientId("TEST-0001")).hasValue("p1");
		assertThat(config.findPanelIdForClientId("TEST-0002")).hasValue("p2");
	}
}
