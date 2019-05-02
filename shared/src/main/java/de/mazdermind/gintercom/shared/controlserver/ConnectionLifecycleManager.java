package de.mazdermind.gintercom.shared.controlserver;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.connection.ControlServerClient;
import de.mazdermind.gintercom.shared.controlserver.connection.ControlServerSessionTransportErrorEvent;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryService;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.shared.controlserver.events.AddressDiscoveryEvent;
import de.mazdermind.gintercom.shared.controlserver.events.AwaitingProvisioningEvent;
import de.mazdermind.gintercom.shared.controlserver.events.ConnectingEvent;
import de.mazdermind.gintercom.shared.controlserver.events.support.ConnectionLifecycleEventMulticaster;
import de.mazdermind.gintercom.shared.controlserver.events.OperationalEvent;
import de.mazdermind.gintercom.shared.controlserver.messages.ohai.OhaiMessage;
import de.mazdermind.gintercom.shared.controlserver.provisioning.ProvisioningInformation;
import de.mazdermind.gintercom.shared.controlserver.provisioning.ProvisioningInformationAware;

@Component
@ConditionalOnBean(GintercomClientConfiguration.class)
public class ConnectionLifecycleManager implements ProvisioningInformationAware {
	private static final int DISCOVERY_RETRY_INTERVAL_SECONDS = 5;
	private static final int DISCOVERY_INITIAL_DELAY_SECONDS = 2;

	private static Logger log = LoggerFactory.getLogger(ConnectionLifecycleManager.class);

	private final ConnectionLifecycleEventMulticaster connectionLifecycleEventMulticaster;
	private final MatrixAddressDiscoveryService addressDiscoveryService;
	private final ControlServerClient controlServerClient;
	private final GintercomClientConfiguration clientConfiguration;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private ConnectionLifecycle lifecycle = ConnectionLifecycle.STARTING;
	private ScheduledFuture<?> discoverySchedule;

	public ConnectionLifecycleManager(
		@Autowired ConnectionLifecycleEventMulticaster connectionLifecycleEventMulticaster,
		@Autowired MatrixAddressDiscoveryService addressDiscoveryService,
		@Autowired ControlServerClient controlServerClient,
		@Autowired GintercomClientConfiguration clientConfiguration
	) {
		this.connectionLifecycleEventMulticaster = connectionLifecycleEventMulticaster;
		this.addressDiscoveryService = addressDiscoveryService;
		this.controlServerClient = controlServerClient;
		this.clientConfiguration = clientConfiguration;
	}

	public ConnectionLifecycle getLifecycle() {
		return lifecycle;
	}

	@PostConstruct
	public void initiateDiscovery() {
		lifecycle = ConnectionLifecycle.DISCOVERY;

		log.info("Starting Discovery-Scheduler");
		discoverySchedule = scheduler
			.scheduleWithFixedDelay(this::discoveryTryNext, DISCOVERY_INITIAL_DELAY_SECONDS, DISCOVERY_RETRY_INTERVAL_SECONDS, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void stopDiscovery() throws InterruptedException {
		log.info("Shutting down Discovery-Scheduler");
		scheduler.shutdownNow();
		scheduler.awaitTermination(30, TimeUnit.SECONDS);
	}

	private void discoveryTryNext() {
		MatrixAddressDiscoveryServiceImplementation discoveryImplementation = addressDiscoveryService.getNextImplementation();
		log.info("Trying {}", discoveryImplementation.getClass().getSimpleName());
		connectionLifecycleEventMulticaster.dispatch(new AddressDiscoveryEvent(
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
		connectionLifecycleEventMulticaster.dispatch(new ConnectingEvent(discoveredMatrix.getAddress(), discoveredMatrix.getPort()));

		Optional<StompSession> stompSession = controlServerClient.connect(
			discoveredMatrix.getAddress(),
			discoveredMatrix.getPort()
		);

		if (!stompSession.isPresent()) {
			log.info("Connection failed, Re-Starting Discovery");
			lifecycle = ConnectionLifecycle.DISCOVERY;

			log.info("Restarting Discovery-Scheduler");
			discoverySchedule = scheduler
				.scheduleWithFixedDelay(this::discoveryTryNext, DISCOVERY_RETRY_INTERVAL_SECONDS, DISCOVERY_RETRY_INTERVAL_SECONDS, TimeUnit.SECONDS);
		} else {
			log.info("Connected to {}", discoveredMatrix);
			initiateProvisioning(stompSession.get());
		}
	}

	private void initiateProvisioning(StompSession stompSession) {
		log.info("sending Provisioning-Request (Ohai)");
		lifecycle = ConnectionLifecycle.PROVISIONING;
		connectionLifecycleEventMulticaster.dispatch(new AwaitingProvisioningEvent(clientConfiguration.getClientId()));

		stompSession.send("/ohai", OhaiMessage.fromClientConfiguration(clientConfiguration));
	}

	@EventListener
	public void transportErrorEventHandler(ControlServerSessionTransportErrorEvent errorEvent) {
		if (lifecycle == ConnectionLifecycle.PROVISIONING || lifecycle == ConnectionLifecycle.OPERATIONAL) {
			log.info("ControlServer-Connection failed: {} -- disconnecting and restarting Discovery", errorEvent.getMessage());
			controlServerClient.disconnect();
			initiateDiscovery();
		}
	}

	@Override
	public void handleProvisioningInformation(ProvisioningInformation provisioningInformation) {
		log.info("Provisioning received, Client is now Operational");
		lifecycle = ConnectionLifecycle.OPERATIONAL;
		connectionLifecycleEventMulticaster.dispatch(new OperationalEvent());
	}
}
