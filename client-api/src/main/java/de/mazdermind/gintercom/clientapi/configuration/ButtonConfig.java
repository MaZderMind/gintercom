package de.mazdermind.gintercom.clientapi.configuration;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ButtonConfig {
	@NotNull
	private String display;

	@NotNull
	private String target;

	@NotNull
	private ButtonAction action;

	@NotNull
	private ButtonDirection direction;

	@NotNull
	private ButtonTargetType targetType;
}
