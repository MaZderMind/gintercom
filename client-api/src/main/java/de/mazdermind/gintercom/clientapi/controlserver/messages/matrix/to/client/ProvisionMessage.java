package de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client;

import static java.util.Collections.emptyMap;

import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

	@NotNull
	private Set<String> rxGroups;

	@NotNull
	private Set<String> txGroups;
}
