package de.mazdermind.gintercom.shared.controlserver.events;

public class MatrixAddressDiscoveryEvent implements MatrixConnectionLifecycleEvent {
	private final String implementationId;
	private final String implementationName;

	public MatrixAddressDiscoveryEvent(String implementationId, String implementationName) {

		this.implementationId = implementationId;
		this.implementationName = implementationName;
	}

	@Override
	public String getMessage() {
		return "Searching for Matrix using " + implementationName + "…";
	}
}
