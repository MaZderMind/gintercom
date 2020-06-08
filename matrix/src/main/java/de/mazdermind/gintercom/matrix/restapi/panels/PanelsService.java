package de.mazdermind.gintercom.matrix.restapi.panels;

import static com.google.common.base.Predicates.not;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PanelsService {
	private final Config config;
	private final AssociatedClientsManager associatedClientsManager;

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

		return associatedClientsManager.isAssociated(hostId);
	}
}
