package de.mazdermind.gintercom.matrix.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.ButtonSetConfig;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;

@Component
public class ButtonSetResolver {
	private final Config config;

	public ButtonSetResolver(
		@Autowired Config config
	) {
		this.config = config;
	}

	public Map<String, ButtonConfig> resolveButtons(PanelConfig panelConfig) {
		Map<String, ButtonConfig> combinedButtons = new HashMap<>();
		for (String buttonsetId : panelConfig.getButtonsets()) {
			ButtonSetConfig buttonSetConfig = config.getButtonsets().get(buttonsetId);
			combinedButtons.putAll(buttonSetConfig.getButtons());
		}

		combinedButtons.putAll(panelConfig.getButtons());
		return combinedButtons;
	}
}
