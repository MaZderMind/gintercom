package de.mazdermind.gintercom.matrix.restapi.devices;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

import javax.annotation.Nullable;

import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class DeviceDto {
	private String hostId;

	@Nullable
	private String panelId = null;
	private InetSocketAddress clientAddress;
	private LocalDateTime firstSeen;

	public DeviceDto(ClientAssociation clientAssociation) {
		hostId = clientAssociation.getHostId();
		clientAddress = clientAssociation.getSocketAddress();
		firstSeen = clientAssociation.getFirstSeen();
	}

	public boolean isProvisioned() {
		return panelId != null;
	}
}
