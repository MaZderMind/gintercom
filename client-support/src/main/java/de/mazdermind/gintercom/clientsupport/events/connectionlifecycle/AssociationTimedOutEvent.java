package de.mazdermind.gintercom.clientsupport.events.connectionlifecycle;

import java.net.InetAddress;

import de.mazdermind.gintercom.clientsupport.connectionlifecycle.ConnectionLifecycle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AssociationTimedOutEvent extends ConnectionLifecycleEvent {
	private InetAddress address;
	private int port;

	@Override
	public String getDisplayText() {
		return "Association Timed Out";
	}

	@Override
	public String getDetailsText() {
		return "Restarting Discovery";
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.DISCOVERY;
	}
}
