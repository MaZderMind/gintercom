package de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client;

import static java.util.Collections.emptyMap;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProvisionMessage {
	@NotEmpty
	private String display;

	@Valid
	private Map<String, ButtonConfig> buttons = emptyMap();
}
