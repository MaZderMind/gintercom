package de.mazdermind.gintercom.clientsupport.controlserver;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientsupport.controlserver.events.connectionlifecycle.ConnectionLifecycleEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.provision.DeProvisionEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.provision.ProvisionEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventLogger {
	@EventListener
	public void logConnectionLifecycleEvent(ConnectionLifecycleEvent lifecycleEvent) {
		log.debug("ConnectionLifecycleEvent: {}, Lifecycle-Phase: {}",
			lifecycleEvent.getClass().getSimpleName(),
			lifecycleEvent.getLifecycle());
	}

	@EventListener
	public void logProvisioningInformation(ProvisionEvent provisionEvent) {
		log.debug("ProvisionEvent: {}", provisionEvent);
	}

	@EventListener
	public void logDeProvisioning(DeProvisionEvent deProvisionEvent) {
		log.debug("DeProvisionEvent: {}", deProvisionEvent);
	}
}
