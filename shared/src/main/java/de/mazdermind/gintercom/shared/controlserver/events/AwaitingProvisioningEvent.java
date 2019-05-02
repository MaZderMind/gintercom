package de.mazdermind.gintercom.shared.controlserver.events;

import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycle;

public class AwaitingProvisioningEvent implements ConnectionLifecycleEvent {
	private final String clientId;

	public AwaitingProvisioningEvent(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String getDisplayText() {
		return "Awaiting Provisioning";
	}

	@Override
	public String getDetailsText() {
		return "Client-ID: " + clientId;
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.PROVISIONING;
	}

	public String getClientId() {
		return clientId;
	}
}
