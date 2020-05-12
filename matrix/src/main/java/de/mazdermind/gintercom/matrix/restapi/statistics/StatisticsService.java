package de.mazdermind.gintercom.matrix.restapi.statistics;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionInformation;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {
	private final Config config;
	private final PanelConnectionManager panelConnectionManager;

	public StatisticsDto collectStatistics() {
		return new StatisticsDto()
			.setGroupsConfigured(config.getGroups().keySet().stream()
				.sorted().collect(Collectors.toList()))

			.setPanelsAssigned(config.getPanels().entrySet().stream()
				.filter(entry -> entry.getValue().getHostId() != null)
				.map(Map.Entry::getKey)
				.sorted()
				.collect(Collectors.toList()))
			.setPanelsConfigured(config.getPanels().keySet().stream()
				.sorted().collect(Collectors.toList()))
			.setPanelsOnline(config.getPanels().entrySet().stream()
				.filter(entry -> isPanelOnline(entry.getValue()))
				.map(Map.Entry::getKey)
				.sorted()
				.collect(Collectors.toList()))

			.setDevicesOnline(panelConnectionManager.getConnectedPanels().stream()
				.map(PanelConnectionInformation::getHostId)
				.sorted()
				.collect(Collectors.toList()))
			.setDevicesProvisioned(panelConnectionManager.getConnectedPanels().stream()
				.filter(PanelConnectionInformation::isAssignedToPanel)
				.map(PanelConnectionInformation::getHostId)
				.sorted()
				.collect(Collectors.toList()));
	}

	private boolean isPanelOnline(PanelConfig config) {
		String hostId = config.getHostId();
		if (hostId == null) {
			return false;
		}

		Optional<PanelConnectionInformation> connectionInformation = panelConnectionManager.getConnectionInformationForHostId(hostId);
		return connectionInformation.isPresent();
	}

}
