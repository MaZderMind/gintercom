package de.mazdermind.gintercom.shared.controlserver.events;

import java.net.InetAddress;

import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycle;

public class ConnectingEvent implements ConnectionLifecycleEvent {

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
}
