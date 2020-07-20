package de.mazdermind.gintercom.matrix.tools;

import static com.oblac.nomen.Nomen.randomName;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;

public class RandomPanelConfigBuilder {
	public static PanelConfig randomPanelConfig() {
		return new PanelConfig()
			.setClientId(TestClientIdGenerator.generateTestClientId())
			.setDisplay("panel_" + randomName());
	}
}
