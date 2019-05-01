package de.mazdermind.gintercom.shared.controlserver;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.connection.ControlServerClient;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryService;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.shared.controlserver.events.MatrixConnectingEvent;

@Component
@ConditionalOnBean(GintercomClientConfiguration.class)
public class ConnectionLifecycleManager {
	private static Logger log = LoggerFactory.getLogger(ConnectionLifecycleManager.class);
	private final MatrixAddressDiscoveryService addressDiscoveryService;
	private final ControlServerClient controlServerClient;
	private final ApplicationEventPublisher eventPublisher;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private ConnectionLifecycle lifecycle = ConnectionLifecycle.STARTING;
	private ScheduledFuture<?> discoverySchedule;

	public ConnectionLifecycleManager(
		@Autowired ApplicationEventPublisher eventPublisher,
		@Autowired MatrixAddressDiscoveryService addressDiscoveryService,
		@Autowired ControlServerClient controlServerClient
	) {
		this.eventPublisher = eventPublisher;
		this.addressDiscoveryService = addressDiscoveryService;
		this.controlServerClient = controlServerClient;
	}

	public ConnectionLifecycle getLifecycle() {
		return lifecycle;
	}

	@PostConstruct
	public void initiateDiscovery() {
		lifecycle = ConnectionLifecycle.DISCOVERY;

		log.info("Starting Discovery-Scheduler");
		discoverySchedule = scheduler.scheduleWithFixedDelay(this::discoveryTryNext, 0, 5, TimeUnit.SECONDS);
	}

	private void discoveryTryNext() {
		log.info("(Re)Trying Discovery");

		Optional<MatrixAddressDiscoveryServiceResult> discoveryResult = addressDiscoveryService.tryNext();
		if (discoveryResult.isPresent()) {
			MatrixAddressDiscoveryServiceResult discoveredMatrix = discoveryResult.get();

			discoverySchedule.cancel(false);

			lifecycle = ConnectionLifecycle.CONNECTING;
			eventPublisher.publishEvent(new MatrixConnectingEvent(discoveredMatrix));
			tryConnect(discoveredMatrix);
		}
	}

	private void tryConnect(MatrixAddressDiscoveryServiceResult discoveryResult) {
		// TODO check result
		controlServerClient.connect(
			discoveryResult.getAddress(),
			discoveryResult.getPort()
		);
	}
}
