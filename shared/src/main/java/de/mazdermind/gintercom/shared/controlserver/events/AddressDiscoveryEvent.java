package de.mazdermind.gintercom.shared.controlserver.events;

import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycle;

public class AddressDiscoveryEvent implements ConnectionLifecycleEvent {
	private final String implementationId;
	private final String implementationName;

	public AddressDiscoveryEvent(String implementationId, String implementationName) {
		this.implementationId = implementationId;
		this.implementationName = implementationName;
	}

	@Override
	public String getDisplayText() {
		return "Searching for Matrix";
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.DISCOVERY;
	}

	public String getImplementationId() {
		return implementationId;
	}

	public String getImplementationName() {
		return implementationName;
	}
}
