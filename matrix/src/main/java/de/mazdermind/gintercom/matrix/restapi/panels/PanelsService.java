package de.mazdermind.gintercom.matrix.restapi.panels;

import static com.google.common.base.Predicates.not;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionInformation;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PanelsService {
	private final Config config;
	private final PanelConnectionManager panelConnectionManager;

	public Stream<PanelDto> getConfiguredPanels() {
		return config.getPanels().entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.map(entry -> new PanelDto(
				entry.getKey(), entry.getValue(),
				isPanelOnline(entry.getValue().getHostId())));
	}

	public Stream<PanelDto> getAssignedPanels() {
		return getConfiguredPanels()
			.filter(PanelDto::isAssigned);
	}

	public Stream<PanelDto> getUnassignedPanels() {
		return getConfiguredPanels()
			.filter(not(PanelDto::isAssigned));
	}

	public Stream<PanelDto> getOnlinePanels() {
		return getConfiguredPanels()
			.filter(PanelDto::isOnline);
	}

	public Stream<PanelDto> getOfflinePanels() {
		return getConfiguredPanels()
			.filter(not(PanelDto::isOnline));
	}

	private boolean isPanelOnline(String hostId) {
		if (hostId == null) {
			return false;
		}

		Optional<PanelConnectionInformation> connectionInformation = panelConnectionManager
			.getConnectionInformationForHostId(hostId);

		return connectionInformation.isPresent();
	}
}
