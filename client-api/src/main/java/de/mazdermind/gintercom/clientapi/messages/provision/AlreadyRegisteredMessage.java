package de.mazdermind.gintercom.clientapi.messages.provision;

import java.net.InetAddress;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class AlreadyRegisteredMessage {
	private InetAddress remoteIp;
	private LocalDateTime connectionTime;

	public InetAddress getRemoteIp() {
		return remoteIp;
	}

	public AlreadyRegisteredMessage setRemoteIp(InetAddress remoteIp) {
		this.remoteIp = remoteIp;
		return this;
	}

	public LocalDateTime getConnectionTime() {
		return connectionTime;
	}

	public AlreadyRegisteredMessage setConnectionTime(LocalDateTime connectionTime) {
		this.connectionTime = connectionTime;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(remoteIp, connectionTime);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AlreadyRegisteredMessage that = (AlreadyRegisteredMessage) o;
		return Objects.equal(remoteIp, that.remoteIp) &&
			Objects.equal(connectionTime, that.connectionTime);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("remoteIp", remoteIp)
			.append("connectionTime", connectionTime)
			.toString();
	}
}
