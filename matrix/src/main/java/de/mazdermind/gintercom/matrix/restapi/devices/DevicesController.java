package de.mazdermind.gintercom.matrix.restapi.devices;

import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/devices")
@RequiredArgsConstructor
public class DevicesController {
	private final DevicesService devicesService;

	@GetMapping
	public Stream<DeviceDto> getOnlineDevices() {
		return devicesService.getOnlineDevices();
	}

	@GetMapping("/provisioned")
	public Stream<DeviceDto> getProvisionedDevices() {
		return devicesService.getProvisionedDevices();
	}

	@GetMapping("/unprovisioned")
	public Stream<DeviceDto> getUnprovisionedDevices() {
		return devicesService.getUnprovisionedDevices();
	}
}
