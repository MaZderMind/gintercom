package de.mazdermind.gintercom.clientsupport.controlserver.events.connectionlifecycle;

import java.net.InetAddress;

import de.mazdermind.gintercom.clientsupport.controlserver.ConnectionLifecycle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ConnectingEvent extends ConnectionLifecycleEvent {
	private final InetAddress address;
	private final int port;

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
}
