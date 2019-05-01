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
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.shared.controlserver.events.MatrixAddressDiscoveryEvent;
import de.mazdermind.gintercom.shared.controlserver.events.MatrixConnectingEvent;
import de.mazdermind.gintercom.shared.controlserver.events.MatrixProvisioningEvent;

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
		MatrixAddressDiscoveryServiceImplementation discoveryImplementation = addressDiscoveryService.getNextImplementation();
		log.info("Trying {}", discoveryImplementation.getClass().getSimpleName());
		eventPublisher.publishEvent(new MatrixAddressDiscoveryEvent(
			discoveryImplementation.getClass().getSimpleName(),
			discoveryImplementation.getDisplayName()
		));

		Optional<MatrixAddressDiscoveryServiceResult> discoveryResult = discoveryImplementation.tryDiscovery();
		if (discoveryResult.isPresent()) {
			MatrixAddressDiscoveryServiceResult discoveredMatrix = discoveryResult.get();
			log.info("Discovery found potential Matrix {}", discoveredMatrix);

			discoverySchedule.cancel(false);
			tryConnect(discoveredMatrix);
		}
	}

	private void tryConnect(MatrixAddressDiscoveryServiceResult discoveredMatrix) {
		log.info("Trying to Connect to {}", discoveredMatrix);
		lifecycle = ConnectionLifecycle.CONNECTING;
		eventPublisher.publishEvent(new MatrixConnectingEvent(discoveredMatrix.getAddress(), discoveredMatrix.getPort()));

		boolean connected = controlServerClient.connect(
			discoveredMatrix.getAddress(),
			discoveredMatrix.getPort()
		);

		if (!connected) {
			log.info("Connection failed, Re-Starting Discovery");
			lifecycle = ConnectionLifecycle.DISCOVERY;

			log.info("Restarting Discovery-Scheduler");
			discoverySchedule = scheduler.scheduleWithFixedDelay(this::discoveryTryNext, 0, 5, TimeUnit.SECONDS);
		}

		if (connected) {
			log.info("Connected to {}, sending Provisioning-Request (Ohai)", discoveredMatrix);

			initiateProvisioning();
		}
	}

	private void initiateProvisioning() {
		lifecycle = ConnectionLifecycle.PROVISIONING;
		eventPublisher.publishEvent(new MatrixProvisioningEvent());
	}
}
