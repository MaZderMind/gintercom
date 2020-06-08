package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

import de.mazdermind.gintercom.matrix.portpool.PortSet;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * TODO
 */
@Data
@Accessors(chain = true)
public class ClientAssociation {
	private static final TemporalAmount HEARTBEAT_TIMEOUT = Duration.ofSeconds(15);

	private InetSocketAddress socketAddress;
	private String hostId;
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
