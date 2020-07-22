package de.mazdermind.gintercom.matrix.restapi.panels;

import static com.google.common.base.Predicates.not;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PanelsService {
	private final Config config;
	private final AssociatedClientsManager associatedClientsManager;

	public Stream<PanelDto> getConfiguredPanels() {
		return config.getPanels().entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.map(entry -> buildPanelDto(entry.getKey(), entry.getValue()));
	}

	private PanelDto buildPanelDto(String panelId, PanelConfig panelConfig) {
		Optional<ClientAssociation> clientAssociation = Optional.ofNullable(panelConfig.getClientId())
			.flatMap(associatedClientsManager::findAssociation);

		return new PanelDto()
			.setId(panelId)
			.setClientId(panelConfig.getClientId())
			.setDisplay(panelConfig.getDisplay())
			.setOnline(clientAssociation.isPresent())
			.setClientModel(clientAssociation
				.map(ClientAssociation::getClientModel)
				.orElse(null));
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

}
