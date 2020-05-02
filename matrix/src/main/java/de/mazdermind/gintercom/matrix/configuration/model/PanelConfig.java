package de.mazdermind.gintercom.matrix.configuration.model;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;

public class PanelConfig {
	@NotNull
	private String display;

	private String hostId;

	private Set<String> rxGroups = emptySet();
	private Set<String> txGroups = emptySet();
	private List<String> buttonsets = emptyList();

	@Valid
	private Map<String, ButtonConfig> buttons = emptyMap();

	public String getDisplay() {
		return display;
	}

	public PanelConfig setDisplay(String display) {
		this.display = display;
		return this;
	}

	public String getHostId() {
		return hostId;
	}

	public PanelConfig setHostId(String hostId) {
		this.hostId = hostId;
		return this;
	}

	public Set<String> getRxGroups() {
		return rxGroups;
	}

	public PanelConfig setRxGroups(Set<String> rxGroups) {
		this.rxGroups = rxGroups;
		return this;
	}

	public Set<String> getTxGroups() {
		return txGroups;
	}

	public PanelConfig setTxGroups(Set<String> txGroups) {
		this.txGroups = txGroups;
		return this;
	}

	public List<String> getButtonsets() {
		return buttonsets;
	}

	public PanelConfig setButtonsets(List<String> buttonsets) {
		this.buttonsets = buttonsets;
		return this;
	}

	public Map<String, ButtonConfig> getButtons() {
		return unmodifiableMap(buttons);
	}

	public PanelConfig setButtons(Map<String, ButtonConfig> buttons) {
		this.buttons = buttons;
		return this;
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
