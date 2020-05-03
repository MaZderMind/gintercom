package de.mazdermind.gintercom.clientsupport.controlserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import de.mazdermind.gintercom.clientsupport.controlserver.events.ConnectionLifecycleEvent;

@Component
public class LoggingConnectionLifecycleEventHandler {
	private static final Logger log = LoggerFactory.getLogger(LoggingConnectionLifecycleEventHandler.class);

	@EventListener
	public void handleGenericConnectionLifecycleEvent(ConnectionLifecycleEvent lifecycleEvent) {
		log.info("ConnectionLifecycleEvent: {}, Lifecycle-Phase: {}",
			lifecycleEvent.getClass().getSimpleName(),
			lifecycleEvent.getLifecycle());
	}

	@EventListener
	public void handleProvisioningInformation(ProvisioningInformation provisioningInformation) {
		log.info("ProvisioningInformation: {}", provisioningInformation);
	}
}
