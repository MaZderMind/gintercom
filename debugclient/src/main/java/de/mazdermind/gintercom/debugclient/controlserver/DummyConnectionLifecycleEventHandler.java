package de.mazdermind.gintercom.debugclient.controlserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycleManager;
import de.mazdermind.gintercom.shared.controlserver.events.MatrixConnectionLifecycleEvent;

@Component
public class DummyConnectionLifecycleEventHandler {
	private static Logger log = LoggerFactory.getLogger(DummyConnectionLifecycleEventHandler.class);


	public DummyConnectionLifecycleEventHandler(
		@Autowired ConnectionLifecycleManager lifecycleManager
	) {
		log.info("Initial Lifecycle-Phase: {} Operational?: {}",
			lifecycleManager.getLifecycle().getDisplayText(),
			lifecycleManager.getLifecycle().isOperational());
	}

	@EventListener
	public void handleLifecycleEvent(MatrixConnectionLifecycleEvent lifecycleEvent) {
		log.info("Event: {}, Lifecycle-Phase: {} Operational?: {}",
			lifecycleEvent.getDisplayText(),
			lifecycleEvent.getLifecycle(),
			lifecycleEvent.getLifecycle().isOperational());
	}
}
