package de.mazdermind.gintercom.matrix.integration.tools.builder;

import static com.oblac.nomen.Nomen.randomName;
import static de.mazdermind.gintercom.shared.hostid.RandomHostIdGenerator.generateRandomHostId;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;

public class RandomPanelConfigBuilder {
	public static PanelConfig randomPanelConfig() {
		return new PanelConfig()
			.setHostId(generateRandomHostId())
			.setDisplay("panel_" + randomName());
	}
}
