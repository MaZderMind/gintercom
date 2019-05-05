package de.mazdermind.gintercom.matrix.configuration.model;

import static java.util.Collections.unmodifiableMap;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

import de.mazdermind.gintercom.shared.configuration.ButtonConfig;

public class ButtonSetConfig {
	@NotNull
	@Valid
	private Map<String, ButtonConfig> buttons;

	public Map<String, ButtonConfig> getButtons() {
		return unmodifiableMap(buttons);
	}

	public ButtonSetConfig setButtons(Map<String, ButtonConfig> buttons) {
		this.buttons = buttons;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(buttons);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ButtonSetConfig that = (ButtonSetConfig) o;
		return Objects.equal(buttons, that.buttons);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("buttons", buttons)
			.toString();
	}
}
