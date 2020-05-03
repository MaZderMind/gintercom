package de.mazdermind.gintercom.clientsupport.controlserver;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import de.mazdermind.gintercom.clientsupport.controlserver.events.ConnectionLifecycleEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingConnectionLifecycleEventHandler {
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
