package de.mazdermind.gintercom.shared.controlserver;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import de.mazdermind.gintercom.shared.controlserver.connection.ControlServerClient;
import de.mazdermind.gintercom.shared.controlserver.connection.ControlServerSessionTransportErrorAware;
import de.mazdermind.gintercom.shared.controlserver.connection.ControlServerSessionTransportErrorEvent;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryService;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.shared.controlserver.events.AddressDiscoveryEvent;
import de.mazdermind.gintercom.shared.controlserver.events.AwaitingProvisioningEvent;
import de.mazdermind.gintercom.shared.controlserver.events.ConnectingEvent;
import de.mazdermind.gintercom.shared.controlserver.events.OperationalEvent;
import de.mazdermind.gintercom.shared.controlserver.events.support.ConnectionLifecycleEventMulticaster;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;
import de.mazdermind.gintercom.shared.controlserver.provisioning.ProvisioningInformation;
import de.mazdermind.gintercom.shared.controlserver.provisioning.ProvisioningInformationAware;

@Component
@ConditionalOnBean(ClientConfiguration.class)
public class ConnectionLifecycleManager implements ProvisioningInformationAware, ControlServerSessionTransportErrorAware {
	private static final int DISCOVERY_RETRY_INTERVAL_SECONDS = 3;

	private static final Logger log = LoggerFactory.getLogger(ConnectionLifecycleManager.class);

	private final ConnectionLifecycleEventMulticaster connectionLifecycleEventMulticaster;
	private final MatrixAddressDiscoveryService addressDiscoveryService;
	private final ControlServerClient controlServerClient;
	private final ClientConfiguration clientConfiguration;
	private final TaskScheduler scheduler;

	private ConnectionLifecycle lifecycle = ConnectionLifecycle.STARTING;
	private ScheduledFuture<?> discoverySchedule;
	private MatrixAddressDiscoveryServiceResult discoveredMatrix = null;

	public ConnectionLifecycleManager(
		@Autowired ConnectionLifecycleEventMulticaster connectionLifecycleEventMulticaster,
		@Autowired MatrixAddressDiscoveryService addressDiscoveryService,
		@Autowired ControlServerClient controlServerClient,
		@Qualifier("gintercomTaskScheduler") @Autowired TaskScheduler scheduler,
		@Autowired ClientConfiguration clientConfiguration
	) {
		this.connectionLifecycleEventMulticaster = connectionLifecycleEventMulticaster;
		this.addressDiscoveryService = addressDiscoveryService;
		this.controlServerClient = controlServerClient;
		this.clientConfiguration = clientConfiguration;
		this.scheduler = scheduler;
	}

	public ConnectionLifecycle getLifecycle() {
		return lifecycle;
	}

	@PostConstruct
	public void initiateDiscovery() {
		lifecycle = ConnectionLifecycle.DISCOVERY;

		log.info("Starting Discovery-Scheduler");
		scheduleDiscovery(true);
	}

	private void scheduleDiscovery(boolean initial) {
		PeriodicTrigger trigger = new PeriodicTrigger(DISCOVERY_RETRY_INTERVAL_SECONDS, TimeUnit.SECONDS);
		trigger.setInitialDelay(initial ? 0 : DISCOVERY_RETRY_INTERVAL_SECONDS);
		discoverySchedule = scheduler.schedule(this::discoveryTryNext, trigger);
	}

	@VisibleForTesting
	void discoveryTryNext() {
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
			scheduleDiscovery(false);
		} else {
			log.info("Connected to {}", discoveredMatrix);
			this.discoveredMatrix = discoveredMatrix;
			initiateProvisioning(stompSession.get());
		}
	}

	private void initiateProvisioning(StompSession stompSession) {
		log.info("sending PanelRegistrationMessage");
		lifecycle = ConnectionLifecycle.PROVISIONING;
		connectionLifecycleEventMulticaster.dispatch(new AwaitingProvisioningEvent(clientConfiguration.getHostId()));

		stompSession.send("/registration", PanelRegistrationMessage.fromClientConfiguration(clientConfiguration));
	}

	@Override
	public void handleTransportErrorEvent(ControlServerSessionTransportErrorEvent transportErrorEvent) {
		if (lifecycle == ConnectionLifecycle.PROVISIONING || lifecycle == ConnectionLifecycle.OPERATIONAL) {
			log.info("ControlServer-Connection failed: {}", transportErrorEvent.getMessage());
			this.discoveredMatrix = null;
			controlServerClient.disconnect();

			log.info("Restarting Discovery-Scheduler");
			lifecycle = ConnectionLifecycle.DISCOVERY;
			scheduleDiscovery(false);
		}
	}

	@Override
	public void handleProvisioningInformation(ProvisioningInformation provisioningInformation) {
		log.info("Provisioning received, Client is now Operational");
		lifecycle = ConnectionLifecycle.OPERATIONAL;
		connectionLifecycleEventMulticaster.dispatch(new OperationalEvent());
	}

	public Optional<MatrixAddressDiscoveryServiceResult> getDiscoveredMatrix() {
		return Optional.ofNullable(discoveredMatrix);
	}
}
