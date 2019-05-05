package de.mazdermind.gintercom.shared.controlserver.events;

import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycle;

public class AwaitingProvisioningEvent implements ConnectionLifecycleEvent {
	private final String hostId;

	public AwaitingProvisioningEvent(String hostId) {
		this.hostId = hostId;
	}

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

	public String getHostId() {
		return hostId;
	}
}
