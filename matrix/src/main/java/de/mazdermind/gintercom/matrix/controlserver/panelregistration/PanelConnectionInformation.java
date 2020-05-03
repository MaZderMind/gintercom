package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import java.net.InetAddress;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PanelConnectionInformation {
	private String hostId;
	private String sessionId;
	private InetAddress remoteIp;
	private LocalDateTime connectionTime;
}
