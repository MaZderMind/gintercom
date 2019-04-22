package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.constraints.NotNull;

public class GroupConfig {
	@NotNull
	private String display;

	public String getDisplay() {
		return display;
	}
}
