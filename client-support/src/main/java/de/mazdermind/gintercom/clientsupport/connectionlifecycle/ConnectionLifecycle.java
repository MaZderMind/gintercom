package de.mazdermind.gintercom.clientsupport.connectionlifecycle;

public enum ConnectionLifecycle {
	STARTING,
	DISCOVERY,
	ASSOCIATING,
	PROVISIONING,
	OPERATIONAL,
	DEASSOCIATED;

	public boolean isOperational() {
		return this == OPERATIONAL;
	}

	public boolean isConnected() {
		return this == PROVISIONING || this == OPERATIONAL;
	}
}
