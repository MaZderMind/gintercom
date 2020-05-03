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
	private ButtonAction action;

	@NotNull
	private ButtonTargetType targetType;

	@NotNull
	private String target;
}
