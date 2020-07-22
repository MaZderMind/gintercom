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
	private ButtonTargetType targetType = ButtonTargetType.GROUP;

	@NotNull
	private ButtonAction action = ButtonAction.PUSH;

	@NotNull
	private ButtonDirection direction = ButtonDirection.TX;
}
