package de.mazdermind.gintercom.clientsupport.controlserver.events;

import java.net.InetAddress;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

import de.mazdermind.gintercom.clientsupport.controlserver.ConnectionLifecycle;

public class ConnectingEvent extends ConnectionLifecycleEvent {

	private final InetAddress address;
	private final int port;

	public ConnectingEvent(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	@Override
	public String getDisplayText() {
		return "Trying to Connect to Matrix";
	}

	@Override
	public String getDetailsText() {
		return String.format("at %s (%s:%d)",
			address.getHostName(),
			address.getHostAddress(),
			port);
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.CONNECTING;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(address, port);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConnectingEvent that = (ConnectingEvent) o;
		return port == that.port &&
			Objects.equal(address, that.address);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("address", address)
			.append("port", port)
			.toString();
	}
}
