package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Optional;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PanelConnectionInformation {
	private String hostId;
	private String sessionId;
	private Optional<String> panelId = Optional.empty();
	private InetAddress remoteIp;
	private LocalDateTime connectionTime;

	public boolean isAssignedToPanel() {
		return panelId.isPresent();
	}
}
