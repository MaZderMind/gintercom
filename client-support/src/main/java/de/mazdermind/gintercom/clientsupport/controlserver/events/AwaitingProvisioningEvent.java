package de.mazdermind.gintercom.clientsupport.controlserver.events;

import de.mazdermind.gintercom.clientsupport.controlserver.ConnectionLifecycle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AwaitingProvisioningEvent extends ConnectionLifecycleEvent {
	private final String hostId;

	@Override
	public String getDisplayText() {
		return "Awaiting Provisioning";
	}

	@Override
	public String getDetailsText() {
		return "Host-ID: " + hostId;
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.PROVISIONING;
	}
}
