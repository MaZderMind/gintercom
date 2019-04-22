package de.mazdermind.gintercom.matrix.configuration.model;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;

import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PanelConfig {
	@NotNull
	private String display;

	private Set<String> rxGroups = emptySet();
	private Set<String> txGroups = emptySet();
	private Set<String> buttonsets = emptySet();

	@Valid
	private PanelFixedIpConfiguration fixedIp = null;

	@Valid
	private Map<String, ButtonConfig> buttons = emptyMap();

	public String getDisplay() {
		return display;
	}

	public Set<String> getRxGroups() {
		return rxGroups;
	}

	public Set<String> getTxGroups() {
		return txGroups;
	}

	public Set<String> getButtonsets() {
		return buttonsets;
	}

	public PanelFixedIpConfiguration getFixedIp() {
		return fixedIp;
	}

	public boolean hasFixedIp() {
		return fixedIp != null;
	}

	public Map<String, ButtonConfig> getButtons() {
		return unmodifiableMap(buttons);
	}
}
