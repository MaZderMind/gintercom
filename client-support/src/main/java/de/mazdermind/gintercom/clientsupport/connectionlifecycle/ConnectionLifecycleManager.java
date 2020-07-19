package de.mazdermind.gintercom.clientsupport.connectionlifecycle;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import de.mazdermind.gintercom.clientapi.configuration.ClientConfiguration;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeProvisionMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ProvisionMessage;
import de.mazdermind.gintercom.clientsupport.controlserver.ClientAssociationManager;
import de.mazdermind.gintercom.clientsupport.discovery.MatrixAddressDiscoveryService;
import de.mazdermind.gintercom.clientsupport.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.clientsupport.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.clientsupport.events.AssociatedEvent;
import de.mazdermind.gintercom.clientsupport.events.BeforeClientShutdownEvent;
import de.mazdermind.gintercom.clientsupport.events.DeAssociatedEvent;
import de.mazdermind.gintercom.clientsupport.events.connectionlifecycle.AddressDiscoveryEvent;
import de.mazdermind.gintercom.clientsupport.events.connectionlifecycle.AssociatingEvent;
import de.mazdermind.gintercom.clientsupport.events.connectionlifecycle.AssociationTimedOutEvent;
import de.mazdermind.gintercom.clientsupport.events.connectionlifecycle.AwaitingProvisioningEvent;
import de.mazdermind.gintercom.clientsupport.events.connectionlifecycle.DisconnectedEvent;
import de.mazdermind.gintercom.clientsupport.events.connectionlifecycle.OperationalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionLifecycleManager {
	private static final Duration DISCOVERY_RETRY_INTERVAL = Duration.ofSeconds(2);
	private static final Duration ASSOCIATION_TIMEOUT = Duration.ofSeconds(5);

	private final ApplicationEventPublisher eventPublisher;
	private final MatrixAddressDiscoveryService addressDiscoveryService;
	private final TaskScheduler scheduler;
	private final ClientAssociationManager clientAssociationManager;
	private final ClientConfiguration clientConfiguration;

	private ConnectionLifecycle lifecycle = ConnectionLifecycle.STARTING;
	private ScheduledFuture<?> scheduledAssociationTimeout;
	private boolean shutdown = false;

	public ConnectionLifecycle getLifecycle() {
		return lifecycle;
	}

	@EventListener(ContextRefreshedEvent.class)
	public void initiateDiscovery() {
		lifecycle = ConnectionLifecycle.DISCOVERY;

		log.info("Starting Matrix-Discovery");
		discoveryTryNext();
	}

	@VisibleForTesting
	void discoveryTryNext() {
		MatrixAddressDiscoveryServiceImplementation discoveryImplementation = addressDiscoveryService.getNextImplementation();
		log.info("Trying {}", discoveryImplementation.getClass().getSimpleName());
		eventPublisher.publishEvent(new AddressDiscoveryEvent()
			.setImplementationId(discoveryImplementation.getClass().getSimpleName())
			.setImplementationName(discoveryImplementation.getDisplayName()));

		Optional<MatrixAddressDiscoveryServiceResult> discoveryResult = discoveryImplementation.tryDiscovery();
		if (discoveryResult.isPresent()) {
			MatrixAddressDiscoveryServiceResult discoveredMatrix = discoveryResult.get();
			log.info("Discovery found potential Matrix {}", discoveredMatrix);

			tryAssociate(discoveredMatrix);
		} else {
			scheduleDiscoveryRetry();
		}
	}

	private void tryAssociate(MatrixAddressDiscoveryServiceResult discoveredMatrix) {
		lifecycle = ConnectionLifecycle.ASSOCIATING;

		log.info("Initiating Association with {}", discoveredMatrix);
		eventPublisher.publishEvent(new AssociatingEvent()
			.setSocketAddress(discoveredMatrix.getSocketAddress()));

		clientAssociationManager.initiateAssociation(discoveredMatrix.getSocketAddress());
		scheduledAssociationTimeout = scheduler.schedule(this::associationTimedOut,
			ZonedDateTime.now().plus(ASSOCIATION_TIMEOUT).toInstant());
	}

	public void associationTimedOut() {
		if (lifecycle != ConnectionLifecycle.ASSOCIATING) {
			log.warn("associationTimedOut in wrong Lifecycle Phase: {}", lifecycle);
			return;
		}

		scheduledAssociationTimeout = null;

		log.info("Association timed out");
		eventPublisher.publishEvent(new AssociationTimedOutEvent());

		scheduleDiscoveryRetry();
	}

	@EventListener(AssociatedEvent.class)
	public void handleAssociatedEvent() {
		if (lifecycle != ConnectionLifecycle.ASSOCIATING) {
			log.warn("handleAssociatedEvent in wrong Lifecycle Phase: {}", lifecycle);
			return;
		}

		scheduledAssociationTimeout.cancel(false);
		scheduledAssociationTimeout = null;

		log.info("Received AssociatedEvent, Client is now waiting for Provisioning");
		eventPublisher.publishEvent(new AwaitingProvisioningEvent()
			.setHostId(clientConfiguration.getHostId()));

		lifecycle = ConnectionLifecycle.PROVISIONING;
	}

	@EventListener(ProvisionMessage.class)
	public void handleProvisionMessage() {
		if (lifecycle != ConnectionLifecycle.PROVISIONING) {
			log.warn("handleProvisionMessage in wrong Lifecycle Phase: {}", lifecycle);
			return;
		}

		log.info("Received ProvisionMessage, Client is now Operational");
		eventPublisher.publishEvent(new OperationalEvent());

		lifecycle = ConnectionLifecycle.OPERATIONAL;
	}

	@EventListener(DeProvisionMessage.class)
	public void handleDeProvisionMessage() {
		if (lifecycle != ConnectionLifecycle.OPERATIONAL) {
			log.warn("handleDeProvisionMessage in wrong Lifecycle Phase: {}", lifecycle);
			return;
		}

		eventPublisher.publishEvent(new AwaitingProvisioningEvent()
			.setHostId(clientConfiguration.getHostId()));

		log.info("Received DeProvisionMessage, Client is now waiting for Provisioning");
		lifecycle = ConnectionLifecycle.PROVISIONING;
	}

	@EventListener(DeAssociatedEvent.class)
	public void handleDeAssociatedEvent() {
		if (lifecycle != ConnectionLifecycle.OPERATIONAL && lifecycle != ConnectionLifecycle.PROVISIONING) {
			log.warn("handleDeAssociatedEvent in wrong Lifecycle Phase: {}", lifecycle);
			return;
		}

		log.info("Received DeAssociatedEvent, Client is now De-Associated");
		eventPublisher.publishEvent(new DisconnectedEvent());

		lifecycle = ConnectionLifecycle.DEASSOCIATED;
		scheduleDiscoveryRetry();
	}

	@EventListener(BeforeClientShutdownEvent.class)
	public void handleBeforeShutdownEvent() {
		shutdown = true;
	}

	private void scheduleDiscoveryRetry() {
		if (shutdown) {
			return;
		}

		log.info("Scheduling Re-Discovery");
		scheduler.schedule(this::discoveryTryNext, ZonedDateTime.now().plus(DISCOVERY_RETRY_INTERVAL).toInstant());
	}
}
