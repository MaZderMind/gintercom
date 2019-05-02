package de.mazdermind.gintercom.shared.controlserver.events.support;

import static de.mazdermind.gintercom.shared.utils.ObjectListClassNameUtil.classNamesList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.shared.controlserver.events.AddressDiscoveryEvent;
import de.mazdermind.gintercom.shared.controlserver.events.AwaitingProvisioningEvent;
import de.mazdermind.gintercom.shared.controlserver.events.ConnectingEvent;
import de.mazdermind.gintercom.shared.controlserver.events.OperationalEvent;

@Service
@Lazy
public class ConnectionLifecycleEventMulticaster {
	private static Logger log = LoggerFactory.getLogger(ConnectionLifecycleEventMulticaster.class);
	private final List<ConnectionLifecycleEventAware> connectionLifecycleEventAwares;

	public ConnectionLifecycleEventMulticaster(
		@Autowired List<ConnectionLifecycleEventAware> connectionLifecycleEventAwares
	) {
		log.info("Found {} ConnectionLifecycleEventAware Implementations: {}",
			connectionLifecycleEventAwares.size(), classNamesList(connectionLifecycleEventAwares));
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
