package de.mazdermind.gintercom.clientsupport.controlserver;

public enum ConnectionLifecycle {
	STARTING,
	DISCOVERY,
	CONNECTING,
	PROVISIONING,
	OPERATIONAL,
	DISCONNECTED;

	public boolean isOperational() {
		return this == OPERATIONAL;
	}
}
