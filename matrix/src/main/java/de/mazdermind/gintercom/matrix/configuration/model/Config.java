package de.mazdermind.gintercom.matrix.configuration.model;

import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static Logger log = LoggerFactory.getLogger(Config.class);

	@Valid
	private MatrixConfig matrixConfig;

	@Valid
	private Map<String, PanelConfig> panels;

	@Valid
	private Map<String, GroupConfig> groups;

	@Valid
	private Map<String, ButtonSetConfig> buttonsets;

	public Config(MatrixConfig matrixConfig, Map<String, PanelConfig> panels, Map<String, GroupConfig> groups, Map<String, ButtonSetConfig> buttonsets) {
		this.matrixConfig = matrixConfig;
		this.panels = panels;
		this.groups = groups;
		this.buttonsets = buttonsets;
	}

	public MatrixConfig getMatrixConfig() {
		return matrixConfig;
	}

	public Map<String, PanelConfig> getPanels() {
		return panels;
	}

	public Map<String, GroupConfig> getGroups() {
		return groups;
	}

	public Map<String, ButtonSetConfig> getButtonsets() {
		return buttonsets;
	}

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
		panels.forEach((panelId, panel) -> {
			panel.getButtonsets().forEach(buttonset -> {
				if (!buttonsets.containsKey(buttonset)) {
					throw new ValidationException(String.format(
							"ButtonSet %s referenced from Panel %s does not exist",
							buttonset, panelId));
				}
			});
		});

		log.debug("Validating Panel-Button-Target references");
		panels.forEach((panelId, panel) -> {
			validateButtonReferences(panel.getButtons(), String.format("Panel %s", panelId));
		});

		log.debug("Validating ButtonSet-Button-Target references");
		buttonsets.forEach((buttonSetId, buttonSet) -> {
			validateButtonReferences(buttonSet.getButtons(), String.format("ButtonSet %s", buttonSet));
		});
	}

	private void validateButtonReferences(Map<String, ButtonConfig> buttons, String container) {
		buttons.forEach((buttonId, button) -> {
			String targetId = button.getTarget();
			ButtonTargetType targetType = button.getTargetType();
			if (targetType == ButtonTargetType.GROUP) {
				if (!groups.containsKey(targetId)) {
					throw new ValidationException(String.format(
							"Group %s referenced as target from Button %s of %s does not exist",
							targetId, buttonId, container));
				}
			} else if (targetType == ButtonTargetType.PANEL) {
				if (!panels.containsKey(targetId)) {
					throw new ValidationException(String.format(
							"Panel %s referenced as target from Button %s of %s does not exist",
							targetId, buttonId, container));
				}
			}
		});
	}

	public Optional<String> findPanelIdForHostId(String hostId) {
		return panels.entrySet().stream()
			.filter(entry -> hostId.equals(entry.getValue().getHostId()))
			.findFirst()
			.map(Map.Entry::getKey);
	}
}
