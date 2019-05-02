package de.mazdermind.gintercom.shared.controlserver.events;

/**
 * Implement this Interface to receive ConnectionLifecycleEvents
 */
public interface ConnectionLifecycleEventAware {
	default void handleGenericConnectionLifecycleEvent(ConnectionLifecycleEvent lifecycleEvent) {
	}

	default void handleAddressDiscoveryEvent(AddressDiscoveryEvent addressDiscoveryEvent) {
	}

	default void handleAwaitingProvisioningEvent(AwaitingProvisioningEvent awaitingProvisioningEvent) {
	}

	default void handleConnectingEvent(ConnectingEvent connectingEvent) {
	}

	default void handleOperationalEvent(OperationalEvent operationalEvent) {
	}
}
