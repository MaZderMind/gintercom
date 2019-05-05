package de.mazdermind.gintercom.matrix.configuration.model;

import static java.util.Collections.unmodifiableMap;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class ButtonSetConfig {
	@NotNull
	private Map<String, ButtonConfig> buttons;

	public Map<String, ButtonConfig> getButtons() {
		return unmodifiableMap(buttons);
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
