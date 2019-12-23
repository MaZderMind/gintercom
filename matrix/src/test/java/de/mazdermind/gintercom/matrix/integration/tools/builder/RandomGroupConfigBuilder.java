package de.mazdermind.gintercom.matrix.integration.tools.builder;

import static com.oblac.nomen.Nomen.randomName;

import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;

public class RandomGroupConfigBuilder {
	public static GroupConfig randomGroupConfig() {
		return new GroupConfig()
			.setDisplay("panel_" + randomName());
	}
}
