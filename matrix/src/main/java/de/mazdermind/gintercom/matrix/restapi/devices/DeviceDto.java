package de.mazdermind.gintercom.matrix.restapi.devices;

import java.net.InetAddress;
import java.time.LocalDateTime;

import javax.annotation.Nullable;

import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelConnectionInformation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class DeviceDto {
	private String hostId;
	@Nullable
	private String panelId;
	private InetAddress remoteIp;
	private LocalDateTime connectionTime;

	public DeviceDto(PanelConnectionInformation connectionInformation) {
		hostId = connectionInformation.getHostId();
		panelId = connectionInformation.getPanelId().orElse(null);
		remoteIp = connectionInformation.getRemoteIp();
		connectionTime = connectionInformation.getConnectionTime();
	}

	public boolean isProvisioned() {
		return panelId != null;
	}
}
