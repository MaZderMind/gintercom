package de.mazdermind.gintercom.shared.controlserver.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class ConnectionLifecycleEventMulticaster {
	private final List<ConnectionLifecycleEventAware> connectionLifecycleEventAwares;

	public ConnectionLifecycleEventMulticaster(
		@Autowired List<ConnectionLifecycleEventAware> connectionLifecycleEventAwares
	) {
		this.connectionLifecycleEventAwares = connectionLifecycleEventAwares;
	}

	public void dispatch(AddressDiscoveryEvent addressDiscoveryEvent) {
		connectionLifecycleEventAwares.forEach(connectionLifecycleEventAware -> {
			connectionLifecycleEventAware.handleGenericConnectionLifecycleEvent(addressDiscoveryEvent);
			connectionLifecycleEventAware.handleAddressDiscoveryEvent(addressDiscoveryEvent);
		});
	}

	public void dispatch(AwaitingProvisioningEvent awaitingProvisioningEvent) {
		connectionLifecycleEventAwares.forEach(connectionLifecycleEventAware -> {
			connectionLifecycleEventAware.handleGenericConnectionLifecycleEvent(awaitingProvisioningEvent);
			connectionLifecycleEventAware.handleAwaitingProvisioningEvent(awaitingProvisioningEvent);
		});
	}

	public void dispatch(ConnectingEvent connectingEvent) {
		connectionLifecycleEventAwares.forEach(connectionLifecycleEventAware -> {
			connectionLifecycleEventAware.handleGenericConnectionLifecycleEvent(connectingEvent);
			connectionLifecycleEventAware.handleConnectingEvent(connectingEvent);
		});
	}


	public void dispatch(OperationalEvent operationalEvent) {
		connectionLifecycleEventAwares.forEach(connectionLifecycleEventAware -> {
			connectionLifecycleEventAware.handleGenericConnectionLifecycleEvent(operationalEvent);
			connectionLifecycleEventAware.handleOperationalEvent(operationalEvent);
		});
	}
}
