package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class ButtonConfig {
	@NotNull
	private String display;

	@NotNull
	private ButtonAction action;

	@NotNull
	private ButtonTargetType targetType;

	@NotNull
	private String target;

	public String getDisplay() {
		return display;
	}

	public ButtonAction getAction() {
		return action;
	}

	public ButtonTargetType getTargetType() {
		return targetType;
	}

	public String getTarget() {
		return target;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(display, action, targetType, target);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ButtonConfig that = (ButtonConfig) o;
		return Objects.equal(display, that.display) &&
			action == that.action &&
			targetType == that.targetType &&
			Objects.equal(target, that.target);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("display", display)
			.append("action", action)
			.append("targetType", targetType)
			.append("target", target)
			.toString();
	}
}
