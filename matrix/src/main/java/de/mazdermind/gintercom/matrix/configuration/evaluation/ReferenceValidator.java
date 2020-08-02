package de.mazdermind.gintercom.matrix.configuration.evaluation;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.CommunicationTargetType;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReferenceValidator {
	private ReferenceValidator() {
	}

	public static void validateReferences(Config config) {
		log.debug("Validating Panel -> Group references");
		config.getPanels().forEach((panelId, panel) -> {
			panel.getRxGroups().forEach(rxGroup -> {
				if (!config.getGroups().containsKey(rxGroup)) {
					throw new ValidationException(String.format(
						"Group %s referenced as rxGroup from Panel %s does not exist",
						rxGroup, panelId));
				}
			});

			panel.getTxGroups().forEach(txGroup -> {
				if (!config.getGroups().containsKey(txGroup)) {
					throw new ValidationException(String.format(
						"Group %s referenced as txGroup from Panel %s does not exist",
						txGroup, panelId));
				}
			});
		});

		log.debug("Validating Panel -> ButtonSet references");
		config.getPanels().forEach((panelId, panel) -> panel.getButtonSets().forEach(buttonSet -> {
			if (!config.getButtonSets().containsKey(buttonSet)) {
				throw new ValidationException(String.format(
					"ButtonSet %s referenced from Panel %s does not exist",
					buttonSet, panelId));
			}
		}));

		log.debug("Validating Panel-Button-Target references");
		config.getPanels().forEach((panelId, panel) ->
			validateButtonReferences(config, panel.getButtons(), String.format("Panel %s", panelId)));

		log.debug("Validating ButtonSet-Button-Target references");
		config.getButtonSets().forEach((buttonSetId, buttonSet) ->
			validateButtonReferences(config, buttonSet.getButtons(), String.format("ButtonSet %s", buttonSetId)));

		log.debug("Validating Client-ID references");
		HashMap<String, String> usedClientIds = new HashMap<>();
		config.getPanels().forEach((panelId, value) -> {
			String clientId = value.getClientId();
			String firstUser = usedClientIds.get(clientId);

			if (firstUser != null) {
				throw new ValidationException(String.format(
					"Client-ID %s referenced from Panel %s is also referenced from Panel %s",
					clientId, panelId, firstUser));
			}

			usedClientIds.put(clientId, panelId);
		});
	}

	private static void validateButtonReferences(Config config, Map<String, ButtonConfig> buttons, String container) {
		buttons.forEach((buttonId, button) -> {
			String targetId = button.getTarget();
			CommunicationTargetType targetType = button.getTargetType();
			if (targetType == CommunicationTargetType.GROUP) {
				if (!config.getGroups().containsKey(targetId)) {
					throw new ValidationException(String.format(
						"Group %s referenced as target from Button %s of %s does not exist",
						targetId, buttonId, container));
				}
			} else if (targetType == CommunicationTargetType.PANEL) {
				if (!config.getPanels().containsKey(targetId)) {
					throw new ValidationException(String.format(
						"Panel %s referenced as target from Button %s of %s does not exist",
						targetId, buttonId, container));
				}
			}
		});
	}
}
