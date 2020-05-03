package de.mazdermind.gintercom.clientsupport.controlserver;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import de.mazdermind.gintercom.clientapi.configuration.ClientConfiguration;
import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import de.mazdermind.gintercom.clientapi.messages.registration.PanelRegistrationMessage;
import de.mazdermind.gintercom.clientsupport.controlserver.connection.ControlServerClient;
import de.mazdermind.gintercom.clientsupport.controlserver.connection.ControlServerSessionTransportErrorEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryService;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.clientsupport.controlserver.events.AddressDiscoveryEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.AwaitingProvisioningEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.ConnectingEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.OperationalEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.provisioning.ProvisioningInformationAware;

@Component
public class ConnectionLifecycleManager implements ProvisioningInformationAware {
	private static final int DISCOVERY_RETRY_INTERVAL_SECONDS = 3;

	private static final Logger log = LoggerFactory.getLogger(ConnectionLifecycleManager.class);

	private final ApplicationEventPublisher eventPublisher;
	private final MatrixAddressDiscoveryService addressDiscoveryService;
	private final ControlServerClient controlServerClient;
	private final ClientConfiguration clientConfiguration;
	private final TaskScheduler scheduler;

	private ConnectionLifecycle lifecycle = ConnectionLifecycle.STARTING;
	private ScheduledFuture<?> discoverySchedule;
	private MatrixAddressDiscoveryServiceResult discoveredMatrix = null;

	public ConnectionLifecycleManager(
		@Autowired ApplicationEventPublisher eventPublisher,
		@Autowired MatrixAddressDiscoveryService addressDiscoveryService,
		@Autowired ControlServerClient controlServerClient,
		@Qualifier("gintercomTaskScheduler") @Autowired TaskScheduler scheduler,
		@Autowired ClientConfiguration clientConfiguration
	) {
		this.eventPublisher = eventPublisher;
		this.addressDiscoveryService = addressDiscoveryService;
		this.controlServerClient = controlServerClient;
		this.clientConfiguration = clientConfiguration;
		this.scheduler = scheduler;
	}

	public ConnectionLifecycle getLifecycle() {
		return lifecycle;
	}

	@EventListener(ContextRefreshedEvent.class)
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
		eventPublisher.publishEvent(new AddressDiscoveryEvent(
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
		eventPublisher.publishEvent(new ConnectingEvent(discoveredMatrix.getAddress(), discoveredMatrix.getPort()));

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
		eventPublisher.publishEvent(new AwaitingProvisioningEvent(clientConfiguration.getHostId()));

		stompSession.send("/registration", PanelRegistrationMessage.fromClientConfiguration(clientConfiguration));
	}

	@EventListener
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
		eventPublisher.publishEvent(new OperationalEvent());
	}

	public Optional<MatrixAddressDiscoveryServiceResult> getDiscoveredMatrix() {
		return Optional.ofNullable(discoveredMatrix);
	}
}
