package de.mazdermind.gintercom.matrix.configuration.model;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;

import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

import de.mazdermind.gintercom.shared.configuration.ButtonConfig;

public class PanelConfig {
	@NotNull
	private String display;

	private String hostId;

	private Set<String> rxGroups = emptySet();
	private Set<String> txGroups = emptySet();
	private Set<String> buttonsets = emptySet();

	@Valid
	private Map<String, ButtonConfig> buttons = emptyMap();

	public String getDisplay() {
		return display;
	}

	public String getHostId() {
		return hostId;
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

	public Map<String, ButtonConfig> getButtons() {
		return unmodifiableMap(buttons);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(display, hostId, rxGroups, txGroups, buttonsets, buttons);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PanelConfig that = (PanelConfig) o;
		return Objects.equal(display, that.display) &&
			Objects.equal(hostId, that.hostId) &&
			Objects.equal(rxGroups, that.rxGroups) &&
			Objects.equal(txGroups, that.txGroups) &&
			Objects.equal(buttonsets, that.buttonsets) &&
			Objects.equal(buttons, that.buttons);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("display", display)
			.append("hostId", hostId)
			.append("rxGroups", rxGroups)
			.append("txGroups", txGroups)
			.append("buttonsets", buttonsets)
			.append("buttons", buttons)
			.toString();
	}
}
