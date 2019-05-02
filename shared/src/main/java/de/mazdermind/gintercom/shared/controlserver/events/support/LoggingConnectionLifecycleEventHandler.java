package de.mazdermind.gintercom.shared.controlserver.events.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.events.ConnectionLifecycleEvent;
import de.mazdermind.gintercom.shared.controlserver.provisioning.ProvisioningInformation;
import de.mazdermind.gintercom.shared.controlserver.provisioning.ProvisioningInformationAware;

@Component
public class LoggingConnectionLifecycleEventHandler implements ConnectionLifecycleEventAware, ProvisioningInformationAware {
	private static Logger log = LoggerFactory.getLogger(LoggingConnectionLifecycleEventHandler.class);

	@Override
	public void handleGenericConnectionLifecycleEvent(ConnectionLifecycleEvent lifecycleEvent) {
		log.info("ConnectionLifecycleEvent: {} ({}), Lifecycle-Phase: {} Operational?: {}",
			lifecycleEvent.getClass().getSimpleName(),
			lifecycleEvent.getDisplayText(),
			lifecycleEvent.getLifecycle(),
			lifecycleEvent.getLifecycle().isOperational());
	}


	@Override
	public void handleProvisioningInformation(ProvisioningInformation provisioningInformation) {
		log.info("ProvisioningInformation: {}", provisioningInformation);
	}
}
