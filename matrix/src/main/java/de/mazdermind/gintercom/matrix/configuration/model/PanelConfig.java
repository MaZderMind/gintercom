package de.mazdermind.gintercom.matrix.configuration.model;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PanelConfig {
	@NotNull
	private String display;

	private String clientId;

	private Set<String> rxGroups = emptySet();
	private Set<String> txGroups = emptySet();
	private List<String> buttonsets = emptyList();

	@Valid
	private Map<String, ButtonConfig> buttons = emptyMap();
}
