package de.mazdermind.gintercom.matrix.restapi.devices;

import static com.google.common.base.Predicates.not;

import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.controlserver.AssociatedClientsManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DevicesService {
	private final AssociatedClientsManager associatedClientsManager;
	private final Config config;

	public Stream<DeviceDto> getOnlineDevices() {
		return associatedClientsManager.getAssociations().stream()
			.map(clientAssociation ->
				new DeviceDto(clientAssociation)
					.setPanelId(
						config.findPanelIdForHostId(clientAssociation.getHostId()).orElse(null)
					)
			)
			.sorted(Comparator.comparing(DeviceDto::getFirstSeen).reversed());
	}

	public Stream<DeviceDto> getProvisionedDevices() {
		return getOnlineDevices()
			.filter(DeviceDto::isProvisioned);
	}

	public Stream<DeviceDto> getUnprovisionedDevices() {
		return getOnlineDevices()
			.filter(not(DeviceDto::isProvisioned));
	}
}
