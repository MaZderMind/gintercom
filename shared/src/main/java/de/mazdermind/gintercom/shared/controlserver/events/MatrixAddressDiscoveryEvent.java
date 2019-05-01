package de.mazdermind.gintercom.shared.controlserver.events;

import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycle;

public class MatrixAddressDiscoveryEvent implements MatrixConnectionLifecycleEvent {
	private final String implementationId;
	private final String implementationName;

	public MatrixAddressDiscoveryEvent(String implementationId, String implementationName) {
		this.implementationId = implementationId;
		this.implementationName = implementationName;
	}

	@Override
	public String getDisplayText() {
		return "Searching for Matrix using " + implementationName + "…";
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
