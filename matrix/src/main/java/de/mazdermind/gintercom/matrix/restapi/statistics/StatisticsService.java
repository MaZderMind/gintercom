package de.mazdermind.gintercom.matrix.restapi.statistics;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.restapi.clients.ClientsService;
import de.mazdermind.gintercom.matrix.restapi.groups.GroupsService;
import de.mazdermind.gintercom.matrix.restapi.panels.PanelsService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {
	private final GroupsService groupsService;
	private final PanelsService panelsService;
	private final ClientsService clientsService;

	public StatisticsDto collectStatistics() {
		return new StatisticsDto()
			.setTimestamp(LocalDateTime.now())

			.setGroupsConfigured(groupsService.getConfiguredGroups().count())

			.setPanelsConfigured(panelsService.getConfiguredPanels().count())
			.setPanelsAssigned(panelsService.getAssignedPanels().count())
			.setPanelsUnassigned(panelsService.getUnassignedPanels().count())
			.setPanelsOnline(panelsService.getOnlinePanels().count())
			.setPanelsOffline(panelsService.getOfflinePanels().count())

			.setClientsOnline(clientsService.getOnlineClients().count())
			.setClientsProvisioned(clientsService.getProvisionedClients().count())
			.setClientsUnprovisioned(clientsService.getUnprovisionedClients().count());
	}
}
