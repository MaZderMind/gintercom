package de.mazdermind.gintercom.matrix.restapi.panels;

import java.util.List;
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
public class PanelEditDto {
	@NotEmpty
	private String id;

	@NotNull
	private String display;

	private String clientId;

	@NotEmpty
	private Set<String> rxGroups;

	@NotEmpty
	private Set<String> txGroups;

	@NotEmpty
	private List<String> buttonSets;

	@Valid
	@NotEmpty
	private Map<String, ButtonConfig> buttons;
}
