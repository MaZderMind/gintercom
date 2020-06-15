package de.mazdermind.gintercom.clientsupport.connectionlifecycle;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientsupport.events.connectionlifecycle.ConnectionLifecycleEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConnectionLifecycleEventLogger {
	@EventListener
	public void logConnectionLifecycleEvent(ConnectionLifecycleEvent lifecycleEvent) {
		log.debug("ConnectionLifecycleEvent: {}, Lifecycle-Phase: {}",
			lifecycleEvent.getClass().getSimpleName(),
			lifecycleEvent.getLifecycle());
	}
}
