package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class GroupConfig {
	@NotNull
	private String display;

	public String getDisplay() {
		return display;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(display);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GroupConfig that = (GroupConfig) o;
		return Objects.equal(display, that.display);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("display", display)
			.toString();
	}
}
