package de.mazdermind.gintercom.matrix.restapi.devices;

import static com.google.common.base.Predicates.not;

import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DevicesService {
	private final PanelConnectionManager panelConnectionManager;

	public Stream<DeviceDto> getOnlineDevices() {
		return panelConnectionManager.getConnectedPanels().stream()
			.map(DeviceDto::new)
			.sorted(Comparator.comparing(DeviceDto::getHostId));
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
