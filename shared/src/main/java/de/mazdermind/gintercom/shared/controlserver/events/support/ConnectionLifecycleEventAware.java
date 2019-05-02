package de.mazdermind.gintercom.shared.controlserver.events.support;

import de.mazdermind.gintercom.shared.controlserver.events.AddressDiscoveryEvent;
import de.mazdermind.gintercom.shared.controlserver.events.AwaitingProvisioningEvent;
import de.mazdermind.gintercom.shared.controlserver.events.ConnectingEvent;
import de.mazdermind.gintercom.shared.controlserver.events.ConnectionLifecycleEvent;
import de.mazdermind.gintercom.shared.controlserver.events.OperationalEvent;

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
