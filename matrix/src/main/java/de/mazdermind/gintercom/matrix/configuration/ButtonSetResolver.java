package de.mazdermind.gintercom.matrix.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.matrix.configuration.model.ButtonSetConfig;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ButtonSetResolver {
	private final Config config;

	public Map<String, ButtonConfig> resolveButtons(PanelConfig panelConfig) {
		Map<String, ButtonConfig> combinedButtons = new HashMap<>();
		for (String buttonSetId : panelConfig.getButtonSets()) {
			ButtonSetConfig buttonSetConfig = config.getButtonSets().get(buttonSetId);
			combinedButtons.putAll(buttonSetConfig.getButtons());
		}

		combinedButtons.putAll(panelConfig.getButtons());
		return combinedButtons;
	}
}
