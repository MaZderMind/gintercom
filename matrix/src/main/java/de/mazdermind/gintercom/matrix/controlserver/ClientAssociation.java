package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import de.mazdermind.gintercom.matrix.portpool.PortSet;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ClientAssociation {
	static final Duration HEARTBEAT_TIMEOUT = Duration.ofSeconds(15);

	private InetSocketAddress socketAddress;
	private String clientId;
	private String clientModel;
	private PortSet rtpPorts;
	private LocalDateTime firstSeen;
	private LocalDateTime lastHeartbeat;

	public ClientAssociation() {
		LocalDateTime now = LocalDateTime.now();
		firstSeen = now;
		lastHeartbeat = now;
	}

	public void registerHeartbeat() {
		lastHeartbeat = LocalDateTime.now();
	}

	public boolean isTimedOut() {
		return lastHeartbeat.plus(HEARTBEAT_TIMEOUT).isBefore(LocalDateTime.now());
	}
}
