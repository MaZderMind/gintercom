package de.mazdermind.gintercom.matrix.configuration.model;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.ValidationException;

import com.google.common.collect.Streams;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.CommunicationTargetType;
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

	public void validateReferences() {
		log.debug("Validating Panel -> Group references");
		panels.forEach((panelId, panel) -> {
			panel.getRxGroups().forEach(rxGroup -> {
				if (!groups.containsKey(rxGroup)) {
					throw new ValidationException(String.format(
						"Group %s referenced as rxGroup from Panel %s does not exist",
						rxGroup, panelId));
				}
			});

			panel.getTxGroups().forEach(txGroup -> {
				if (!groups.containsKey(txGroup)) {
					throw new ValidationException(String.format(
						"Group %s referenced as txGroup from Panel %s does not exist",
						txGroup, panelId));
				}
			});
		});

		log.debug("Validating Panel -> ButtonSet references");
		panels.forEach((panelId, panel) -> panel.getButtonSets().forEach(buttonSet -> {
			if (!buttonSets.containsKey(buttonSet)) {
				throw new ValidationException(String.format(
					"ButtonSet %s referenced from Panel %s does not exist",
					buttonSet, panelId));
			}
		}));

		log.debug("Validating Panel-Button-Target references");
		panels.forEach((panelId, panel) ->
			validateButtonReferences(panel.getButtons(), String.format("Panel %s", panelId)));

		log.debug("Validating ButtonSet-Button-Target references");
		buttonSets.forEach((buttonSetId, buttonSet) ->
			validateButtonReferences(buttonSet.getButtons(), String.format("ButtonSet %s", buttonSet)));
	}

	private void validateButtonReferences(Map<String, ButtonConfig> buttons, String container) {
		buttons.forEach((buttonId, button) -> {
			String targetId = button.getTarget();
			CommunicationTargetType targetType = button.getTargetType();
			if (targetType == CommunicationTargetType.GROUP) {
				if (!groups.containsKey(targetId)) {
					throw new ValidationException(String.format(
						"Group %s referenced as target from Button %s of %s does not exist",
						targetId, buttonId, container));
				}
			} else if (targetType == CommunicationTargetType.PANEL) {
				if (!panels.containsKey(targetId)) {
					throw new ValidationException(String.format(
						"Panel %s referenced as target from Button %s of %s does not exist",
						targetId, buttonId, container));
				}
			}
		});
	}

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
