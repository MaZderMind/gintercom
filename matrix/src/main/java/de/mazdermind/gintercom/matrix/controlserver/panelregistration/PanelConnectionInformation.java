package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import java.net.InetAddress;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class PanelConnectionInformation {
	private String hostId;
	private String sessionId;
	private InetAddress remoteIp;
	private LocalDateTime connectionTime;

	public String getHostId() {
		return hostId;
	}

	public PanelConnectionInformation setHostId(String hostId) {
		this.hostId = hostId;
		return this;
	}

	public InetAddress getRemoteIp() {
		return remoteIp;
	}

	public PanelConnectionInformation setRemoteIp(InetAddress remoteIp) {
		this.remoteIp = remoteIp;
		return this;
	}

	public LocalDateTime getConnectionTime() {
		return connectionTime;
	}

	public PanelConnectionInformation setConnectionTime(LocalDateTime connectionTime) {
		this.connectionTime = connectionTime;
		return this;
	}

	public String getSessionId() {
		return sessionId;
	}

	public PanelConnectionInformation setSessionId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hostId, sessionId, remoteIp, connectionTime);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PanelConnectionInformation that = (PanelConnectionInformation) o;
		return Objects.equal(hostId, that.hostId) &&
			Objects.equal(sessionId, that.sessionId) &&
			Objects.equal(remoteIp, that.remoteIp) &&
			Objects.equal(connectionTime, that.connectionTime);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("hostId", hostId)
			.append("sessionId", sessionId)
			.append("remoteIp", remoteIp)
			.append("connectionTime", connectionTime)
			.toString();
	}
}
