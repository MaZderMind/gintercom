package de.mazdermind.gintercom.shared.controlserver.events;

import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycle;

public class AwaitingProvisioningEvent implements ConnectionLifecycleEvent {
	@Override
	public String getDisplayText() {
		return "Awaiting Provisioning";
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.PROVISIONING;
	}
}
