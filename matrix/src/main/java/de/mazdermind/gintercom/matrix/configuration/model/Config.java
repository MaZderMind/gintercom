package de.mazdermind.gintercom.matrix.configuration.model;

import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Accessors(chain = true)
public class Config {
	@Valid
	private MatrixConfig matrixConfig;

	@Valid
	private Map<String, PanelConfig> panels;

	@Valid
	private Map<String, GroupConfig> groups;

	@Valid
	private Map<String, ButtonSetConfig> buttonSets;


	public Optional<String> findPanelIdForClientId(String clientId) {
		return panels.entrySet().stream()
			.filter(entry -> clientId.equals(entry.getValue().getClientId()))
			.findFirst()
			.map(Map.Entry::getKey);
	}
}
