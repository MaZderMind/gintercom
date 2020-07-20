package de.mazdermind.gintercom.matrix.restapi.clients;

import static com.google.common.base.Predicates.not;

import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientsService {
	private final AssociatedClientsManager associatedClientsManager;
	private final Config config;

	public Stream<ClientDto> getOnlineClients() {
		return associatedClientsManager.getAssociations().stream()
			.map(clientAssociation ->
				new ClientDto(clientAssociation)
					.setPanelId(
						config.findPanelIdForClientId(clientAssociation.getClientId()).orElse(null)
					)
			)
			.sorted(Comparator.comparing(ClientDto::getFirstSeen).reversed());
	}

	public Stream<ClientDto> getProvisionedClients() {
		return getOnlineClients()
			.filter(ClientDto::isProvisioned);
	}

	public Stream<ClientDto> getUnprovisionedClients() {
		return getOnlineClients()
			.filter(not(ClientDto::isProvisioned));
	}
}
