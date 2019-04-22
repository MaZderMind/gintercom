package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.constraints.NotNull;

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
}
