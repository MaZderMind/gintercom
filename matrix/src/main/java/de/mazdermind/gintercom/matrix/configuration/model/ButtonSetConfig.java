package de.mazdermind.gintercom.matrix.configuration.model;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ButtonSetConfig {
	@NotNull
	@Valid
	private Map<String, ButtonConfig> buttons;
}
