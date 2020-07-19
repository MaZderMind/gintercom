package de.mazdermind.gintercom.clientsupport.events.connectionlifecycle;

import java.net.InetSocketAddress;

import de.mazdermind.gintercom.clientsupport.connectionlifecycle.ConnectionLifecycle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AssociatingEvent extends ConnectionLifecycleEvent {
	private InetSocketAddress socketAddress;

	@Override
	public String getDisplayText() {
		return "Trying to Associate with Matrix";
	}

	@Override
	public String getDetailsText() {
		return String.format("at %s (%s:%d)",
			socketAddress.getHostName(),
			socketAddress.getAddress(),
			socketAddress.getPort());
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.ASSOCIATING;
	}
}
