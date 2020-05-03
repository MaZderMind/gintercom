package de.mazdermind.gintercom.clientsupport.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

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

public class ConnectionLifecycleManagerTest {

	private ApplicationEventPublisher eventPublisher;
	private MatrixAddressDiscoveryService matrixAddressDiscoveryService;
	private ControlServerClient controlServerClient;
	private ConnectionLifecycleManager connectionLifecycleManager;
	private TaskScheduler taskScheduler;
	private MatrixAddressDiscoveryServiceImplementation matrixAddressDiscoveryServiceImplementation;
	private ScheduledFuture scheduledDiscovery;
	private MatrixAddressDiscoveryServiceResult successfulDiscovery;
	private StompSession stompSession;

	@Before
	public void prepare() throws UnknownHostException {
		taskScheduler = Mockito.mock(TaskScheduler.class);
		scheduledDiscovery = Mockito.mock(ScheduledFuture.class);
		//noinspection unchecked
		Mockito.when(taskScheduler.schedule(ArgumentMatchers.any(), ArgumentMatchers.any(Trigger.class))).thenReturn(scheduledDiscovery);

		matrixAddressDiscoveryServiceImplementation = Mockito.mock(MatrixAddressDiscoveryServiceImplementation.class);
		Mockito.when(matrixAddressDiscoveryServiceImplementation.getDisplayName()).thenReturn("Test-Discovery-Method");
		Mockito.when(matrixAddressDiscoveryServiceImplementation.tryDiscovery()).thenReturn(Optional.empty());

		matrixAddressDiscoveryService = Mockito.mock(MatrixAddressDiscoveryService.class);
		Mockito.when(matrixAddressDiscoveryService.getNextImplementation())
			.thenReturn(matrixAddressDiscoveryServiceImplementation);

		successfulDiscovery = Mockito.mock(MatrixAddressDiscoveryServiceResult.class);
		Mockito.when(successfulDiscovery.getAddress()).thenReturn(InetAddress.getByName("10.56.23.42"));
		Mockito.when(successfulDiscovery.getPort()).thenReturn(2342);

		controlServerClient = Mockito.mock(ControlServerClient.class);
		Mockito.when(controlServerClient.connect(ArgumentMatchers.any(InetAddress.class), ArgumentMatchers.anyInt())).thenReturn(Optional
			.empty());

		eventPublisher = Mockito.mock(ApplicationEventPublisher.class);

		stompSession = Mockito.mock(StompSession.class);

		connectionLifecycleManager = new ConnectionLifecycleManager(
			eventPublisher,
			matrixAddressDiscoveryService,
			controlServerClient,
			taskScheduler, new TestClientConfiguration()
		);
	}

	@Test
	public void initialLifecycleIsStarting() {
		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.STARTING);
	}

	@Test
	public void triesDiscoveryAfterStartup() {
		connectionLifecycleManager.initiateDiscovery();
		Mockito.verify(taskScheduler, Mockito.times(1)).schedule(ArgumentMatchers.any(), ArgumentMatchers.any(Trigger.class));

		connectionLifecycleManager.discoveryTryNext();
		Mockito.verify(matrixAddressDiscoveryService, Mockito.times(1)).getNextImplementation();
		Mockito.verify(matrixAddressDiscoveryServiceImplementation, Mockito.times(1)).tryDiscovery();
	}

	@Test
	public void lifecycleIsDiscovertyAfterStartup() {
		connectionLifecycleManager.initiateDiscovery();
		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.DISCOVERY);
	}

	@Test
	public void retriesDiscoveryAfterFailedDiscovery() {
		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		Mockito.verify(scheduledDiscovery, Mockito.never()).cancel(ArgumentMatchers.anyBoolean());
	}

	@Test
	public void notifiesAboutDiscoveryAttempt() {
		connectionLifecycleManager.discoveryTryNext();
		ArgumentCaptor<AddressDiscoveryEvent> captor = ArgumentCaptor.forClass(AddressDiscoveryEvent.class);
		Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(captor.capture());
		assertThat(captor.getValue().getImplementationName()).isEqualTo("Test-Discovery-Method");
	}

	@Test
	public void triesConnectionAfterSuccessfulDiscovery() {
		setupSuccessfulDiscovery();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		Mockito.verify(controlServerClient, Mockito.times(1)).connect(successfulDiscovery.getAddress(), successfulDiscovery.getPort());
	}

	@Test
	public void cancelsDiscoveryAfterSuccessfulDiscovery() {
		setupSuccessfulDiscovery();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		Mockito.verify(scheduledDiscovery, Mockito.times(1)).cancel(ArgumentMatchers.anyBoolean());
	}

	@Test
	public void restartsDiscoveryAfterFailedConnectionAttempt() {
		setupUnsuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		Mockito.verify(taskScheduler, Mockito.times(2)).schedule(ArgumentMatchers.any(), ArgumentMatchers.any(Trigger.class));
	}

	@Test
	public void lifecycleIsDiscoveryAfterFailedConnectionAttempt() {
		setupUnsuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.DISCOVERY);
	}

	@Test
	public void lifecycleIsConnectingWhileConnectionAttempt() {
		setupSuccessfulDiscovery();

		Mockito.when(controlServerClient.connect(successfulDiscovery.getAddress(), successfulDiscovery.getPort()))
			.then((Answer<Optional<StompSession>>) invocation -> {
				assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.CONNECTING);
				return Optional.empty();
			});

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
	}

	@Test
	public void notifiesAboutConnectionAttempt() {
		setupUnsuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();

		ArgumentCaptor<ConnectingEvent> captor = ArgumentCaptor.forClass(ConnectingEvent.class);
		Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(captor.capture());
		assertThat(captor.getValue().getAddress()).isEqualTo(successfulDiscovery.getAddress());
		assertThat(captor.getValue().getPort()).isEqualTo(successfulDiscovery.getPort());
	}

	@Test
	public void initiatesProvisioningAfterSuccessfulConnection() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();

		ArgumentCaptor<PanelRegistrationMessage> captor = ArgumentCaptor.forClass(PanelRegistrationMessage.class);
		Mockito.verify(stompSession).send(ArgumentMatchers.eq("/registration"), captor.capture());
		PanelRegistrationMessage panelRegistrationMessage = captor.getValue();
		assertThat(panelRegistrationMessage.getHostId()).isEqualTo(TestClientConfiguration.HOST_ID);
		assertThat(panelRegistrationMessage.getClientModel()).isEqualTo(TestClientConfiguration.CLIENT_MODEL);
		assertThat(panelRegistrationMessage.getProtocolVersion()).isEqualTo(TestClientConfiguration.PROTOCOL_VERSION);
		assertThat(panelRegistrationMessage.getCapabilities().getButtons()).isEqualTo(TestClientConfiguration.BUTTONS);
	}

	@Test
	public void notifiesAboutProvisioningAttempt() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();

		ArgumentCaptor<AwaitingProvisioningEvent> captor = ArgumentCaptor.forClass(AwaitingProvisioningEvent.class);
		Mockito.verify(eventPublisher).publishEvent(captor.capture());
		assertThat(captor.getValue().getHostId()).isEqualTo(TestClientConfiguration.HOST_ID);
	}

	@Test
	public void lifecycleIsProvisioningWhileProvisioning() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.PROVISIONING);
	}

	@Test
	public void restartsDiscoveryAfterTransportErrorWhileProvisioning() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		connectionLifecycleManager.handleTransportErrorEvent(Mockito.mock(ControlServerSessionTransportErrorEvent.class));

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.DISCOVERY);
		Mockito.verify(taskScheduler, Mockito.times(2)).schedule(ArgumentMatchers.any(), ArgumentMatchers.any(Trigger.class));
		Mockito.verify(controlServerClient, Mockito.times(1)).disconnect();
	}

	@Test
	public void lifecycleIsOperationalAfterSuccessfulProvisioning() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		connectionLifecycleManager.handleProvisioningInformation(Mockito.mock(ProvisioningInformation.class));

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.OPERATIONAL);
	}

	@Test
	public void notifiesAboutOperationalStateAfterSuccessfulProvisioning() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		connectionLifecycleManager.handleProvisioningInformation(Mockito.mock(ProvisioningInformation.class));

		Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(ArgumentMatchers.any(OperationalEvent.class));
	}

	@Test
	public void restartsDiscoveryAfterTransportErrorWhileOperational() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		connectionLifecycleManager.handleProvisioningInformation(Mockito.mock(ProvisioningInformation.class));
		connectionLifecycleManager.handleTransportErrorEvent(Mockito.mock(ControlServerSessionTransportErrorEvent.class));

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.DISCOVERY);
		Mockito.verify(taskScheduler, Mockito.times(2)).schedule(ArgumentMatchers.any(), ArgumentMatchers.any(Trigger.class));
		Mockito.verify(controlServerClient, Mockito.times(1)).disconnect();
	}

	private void setupSuccessfulDiscovery() {
		Mockito.when(matrixAddressDiscoveryServiceImplementation.tryDiscovery())
			.thenReturn(Optional.of(successfulDiscovery));
	}

	private void setupUnsuccessfulConnection() {
		setupSuccessfulDiscovery();
		Mockito.when(controlServerClient.connect(successfulDiscovery.getAddress(), successfulDiscovery.getPort()))
			.thenReturn(Optional.empty());
	}

	private void setupSuccessfulConnection() {
		setupSuccessfulDiscovery();
		Mockito.when(controlServerClient.connect(successfulDiscovery.getAddress(), successfulDiscovery.getPort()))
			.thenReturn(Optional.of(stompSession));
	}
}
