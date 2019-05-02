package de.mazdermind.gintercom.shared.controlserver.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.messagehandler.DoProvisionEvent;

@Component
public class LoggingConnectionLifecycleEventHandler implements ConnectionLifecycleEventAware {
	private static Logger log = LoggerFactory.getLogger(LoggingConnectionLifecycleEventHandler.class);

	@Override
	public void handleGenericConnectionLifecycleEvent(ConnectionLifecycleEvent lifecycleEvent) {
		log.info("ConnectionLifecycleEvent: {} ({}), Lifecycle-Phase: {} Operational?: {}",
			lifecycleEvent.getClass().getSimpleName(),
			lifecycleEvent.getDisplayText(),
			lifecycleEvent.getLifecycle(),
			lifecycleEvent.getLifecycle().isOperational());
	}

	@EventListener
	public void handleDoProvisionEvent(DoProvisionEvent doProvisionEvent) {
		log.info("DoProvisionEvent");
	}
}
