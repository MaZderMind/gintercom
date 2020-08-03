package de.mazdermind.gintercom.matrix.configuration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

	private Set<String> rxGroups = new HashSet<>();
	private Set<String> txGroups = new HashSet<>();
	private List<String> buttonSets = new ArrayList<>();

	@Valid
	private Map<String, ButtonConfig> buttons = new HashMap<>();
}
