package de.mazdermind.gintercom.matrix.configuration.model;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.google.common.collect.Streams;

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

	public Set<String> getGroupUsers(String groupId) {
		Stream<String> panelUsers = panels.entrySet().stream()
			.filter(entry -> entry.getValue().usesGroup(groupId))
			.map(panel -> "Panel " + panel.getKey());

		Stream<String> buttonSetUsers = buttonSets.entrySet().stream()
			.filter(entry -> entry.getValue().usesGroup(groupId))
			.map(buttonSet -> "Button-Set " + buttonSet.getKey());


		return Streams.concat(panelUsers, buttonSetUsers)
			.collect(Collectors.toSet());
	}
}
