package de.mazdermind.gintercom.matrix.restapi.statistics;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.restapi.devices.DevicesService;
import de.mazdermind.gintercom.matrix.restapi.groups.GroupsService;
import de.mazdermind.gintercom.matrix.restapi.panels.PanelsService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {
	private final GroupsService groupsService;
	private final PanelsService panelsService;
	private final DevicesService devicesService;

	public StatisticsDto collectStatistics() {
		return new StatisticsDto()
			.setGroupsConfigured(groupsService.getConfiguredGroups().count())

			.setPanelsConfigured(panelsService.getConfiguredPanels().count())
			.setPanelsAssigned(panelsService.getAssignedPanels().count())
			.setPanelsUnassigned(panelsService.getUnassignedPanels().count())
			.setPanelsOnline(panelsService.getOnlinePanels().count())
			.setPanelsOffline(panelsService.getOfflinePanels().count())

			.setDevicesOnline(devicesService.getOnlineDevices().count())
			.setDevicesProvisioned(devicesService.getProvisionedDevices().count())
			.setDevicesUnprovisioned(devicesService.getUnprovisionedDevices().count());
	}
}
