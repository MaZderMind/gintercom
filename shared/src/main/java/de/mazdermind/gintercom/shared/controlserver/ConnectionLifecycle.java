package de.mazdermind.gintercom.shared.controlserver;

public enum ConnectionLifecycle {
	STARTING("Starting up…"),
	DISCOVERY("Searching for Matrix…"),
	CONNECTING("Connecting to Matrix…"),
	PROVISIONING("Waiting for Provisioning…"),
	OPERATIONAL("Operational");

	private final String displayText;

	ConnectionLifecycle(String displayText) {

		this.displayText = displayText;
	}

	public boolean isOperational() {
		return this == OPERATIONAL;
	}

	public String getDisplayText() {
		return displayText;
	}
}
