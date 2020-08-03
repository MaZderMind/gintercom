package de.mazdermind.gintercom.matrix.configuration.evaluation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.CommunicationTargetType;
import de.mazdermind.gintercom.matrix.configuration.model.ButtonSetConfig;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;

public class UsageAnalyzer {
	private UsageAnalyzer() {
	}

	public static Set<Usage> getGroupUsages(Config config, String groupId) {
		HashSet<Usage> usages = new HashSet<>();
		config.getPanels().forEach((panelId, panelConfig) ->
			usages.addAll(getGroupUsagesForPanel(panelId, panelConfig, groupId)));

		config.getButtonSets().forEach((buttonSetId, buttonSetConfig) ->
			usages.addAll(getGroupUsagesForButtonSet(buttonSetId, buttonSetConfig, groupId)));


		return usages;
	}

	private static Set<Usage> getGroupUsagesForPanel(String panelId, PanelConfig panelConfig, String groupId) {
		HashSet<Usage> usages = new HashSet<>();
		if (panelConfig.getRxGroups().contains(groupId)) {
			usages.add(new Usage()
				.setUserType(ConfigObjectType.PANEL)
				.setUserId(panelId)
				.setUsageDescription(String.format("Group %s is used as rxGroup of Panel %s", groupId, panelId)));
		}

		if (panelConfig.getTxGroups().contains(groupId)) {
			usages.add(new Usage()
				.setUserType(ConfigObjectType.PANEL)
				.setUserId(panelId)
				.setUsageDescription(String.format("Group %s is used as txGroup of Panel %s", groupId, panelId)));
		}

		usages.addAll(getGroupUsagesForButtons(
			String.format("Panel %s", panelId), panelId, ConfigObjectType.PANEL, groupId, panelConfig.getButtons()));

		return usages;
	}

	private static Set<Usage> getGroupUsagesForButtonSet(String buttonSetId, ButtonSetConfig buttonSetConfig, String groupId) {
		return getGroupUsagesForButtons(String.format("ButtonSet %s", buttonSetId), buttonSetId,
			ConfigObjectType.BUTTON_SET, groupId, buttonSetConfig.getButtons());
	}

	private static Set<Usage> getGroupUsagesForButtons(
		String container, String containerId, ConfigObjectType containerType, String groupId,
		Map<String, ButtonConfig> buttons
	) {
		HashSet<Usage> usages = new HashSet<>();

		buttons.forEach((buttonId, buttonConfig) -> {
			if (buttonConfig.getTargetType() == CommunicationTargetType.GROUP && buttonConfig.getTarget().equals(groupId)) {
				usages.add(new Usage()
					.setUserType(containerType)
					.setUserId(containerId)
					.setUsageDescription(String.format("Group %s is used as target of Button %s of %s",
						groupId, buttonId, container)));
			}
		});

		return usages;
	}
}
