package de.mazdermind.gintercom.matrix.configuration.model;

import static java.util.Collections.unmodifiableMap;

import java.util.Map;

import javax.validation.constraints.NotNull;

public class ButtonSetConfig {
	@NotNull
	private Map<String, ButtonConfig> buttons;

	public Map<String, ButtonConfig> getButtons() {
		return unmodifiableMap(buttons);
	}
}
