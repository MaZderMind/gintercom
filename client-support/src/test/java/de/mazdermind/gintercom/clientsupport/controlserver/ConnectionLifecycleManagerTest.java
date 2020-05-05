package de.mazdermind.gintercom.clientsupport.controlserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

import de.mazdermind.gintercom.clientapi.messages.registration.PanelRegistrationMessage;
import de.mazdermind.gintercom.clientsupport.controlserver.connection.ControlServerClient;
import de.mazdermind.gintercom.clientsupport.controlserver.connection.ControlServerSessionTransportErrorEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryService;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.clientsupport.controlserver.events.connectionlifecycle.AddressDiscoveryEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.connectionlifecycle.AwaitingProvisioningEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.connectionlifecycle.ConnectingEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.connectionlifecycle.DisconnectedEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.connectionlifecycle.OperationalEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.provision.DeProvisionEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.provision.ProvisionEvent;
import de.mazdermind.gintercom.testutils.captors.FilteringArgumentCaptor;

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
		taskScheduler = mock(TaskScheduler.class);
		scheduledDiscovery = mock(ScheduledFuture.class);
		//noinspection unchecked
		Mockito.when(taskScheduler.schedule(any(), any(Trigger.class))).thenReturn(scheduledDiscovery);

		matrixAddressDiscoveryServiceImplementation = mock(MatrixAddressDiscoveryServiceImplementation.class);
		Mockito.when(matrixAddressDiscoveryServiceImplementation.getDisplayName()).thenReturn("Test-Discovery-Method");
		Mockito.when(matrixAddressDiscoveryServiceImplementation.tryDiscovery()).thenReturn(Optional.empty());

		matrixAddressDiscoveryService = mock(MatrixAddressDiscoveryService.class);
		Mockito.when(matrixAddressDiscoveryService.getNextImplementation())
			.thenReturn(matrixAddressDiscoveryServiceImplementation);

		successfulDiscovery = mock(MatrixAddressDiscoveryServiceResult.class);
		Mockito.when(successfulDiscovery.getAddress()).thenReturn(InetAddress.getByName("10.56.23.42"));
		Mockito.when(successfulDiscovery.getPort()).thenReturn(2342);

		controlServerClient = mock(ControlServerClient.class);
		Mockito.when(controlServerClient.connect(any(InetAddress.class), ArgumentMatchers.anyInt())).thenReturn(Optional
			.empty());

		eventPublisher = mock(ApplicationEventPublisher.class);

		stompSession = mock(StompSession.class);

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
		verify(taskScheduler).schedule(any(), any(Trigger.class));

		connectionLifecycleManager.discoveryTryNext();
		verify(matrixAddressDiscoveryService).getNextImplementation();
		verify(matrixAddressDiscoveryServiceImplementation).tryDiscovery();
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
		verify(scheduledDiscovery, Mockito.never()).cancel(ArgumentMatchers.anyBoolean());
	}

	@Test
	public void notifiesAboutDiscoveryAttempt() {
		connectionLifecycleManager.discoveryTryNext();
		ArgumentCaptor<AddressDiscoveryEvent> captor = ArgumentCaptor.forClass(AddressDiscoveryEvent.class);
		verify(eventPublisher).publishEvent(captor.capture());
		assertThat(captor.getValue().getImplementationName()).isEqualTo("Test-Discovery-Method");

		verifyNoMoreInteractions(eventPublisher);
	}

	@Test
	public void triesConnectionAfterSuccessfulDiscovery() {
		setupSuccessfulDiscovery();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		verify(controlServerClient).connect(successfulDiscovery.getAddress(), successfulDiscovery.getPort());
	}

	@Test
	public void cancelsDiscoveryAfterSuccessfulDiscovery() {
		setupSuccessfulDiscovery();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		verify(scheduledDiscovery).cancel(ArgumentMatchers.anyBoolean());
	}

	@Test
	public void restartsDiscoveryAfterFailedConnectionAttempt() {
		setupUnsuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		verify(taskScheduler, times(2)).schedule(any(), any(Trigger.class));
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

		FilteringArgumentCaptor<ConnectingEvent> captor = FilteringArgumentCaptor.forClass(ConnectingEvent.class);
		verify(eventPublisher, atLeast(1)).publishEvent(captor.capture());
		assertThat(captor.getValue().getAddress()).isEqualTo(successfulDiscovery.getAddress());
		assertThat(captor.getValue().getPort()).isEqualTo(successfulDiscovery.getPort());

		verifyNoMoreInteractions(eventPublisher);
	}

	@Test
	public void initiatesProvisioningAfterSuccessfulConnection() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();

		ArgumentCaptor<PanelRegistrationMessage> captor = ArgumentCaptor.forClass(PanelRegistrationMessage.class);
		verify(stompSession).send(ArgumentMatchers.eq("/registration"), captor.capture());
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

		FilteringArgumentCaptor<AwaitingProvisioningEvent> captor = FilteringArgumentCaptor.forClass(AwaitingProvisioningEvent.class);
		verify(eventPublisher, atLeast(1)).publishEvent(captor.capture());
		assertThat(captor.getValue().getHostId()).isEqualTo(TestClientConfiguration.HOST_ID);

		verifyNoMoreInteractions(eventPublisher);
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
		connectionLifecycleManager.handleTransportErrorEvent(mock(ControlServerSessionTransportErrorEvent.class));

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.DISCOVERY);
		verify(taskScheduler, times(2)).schedule(any(Runnable.class), any(Trigger.class));
		verify(controlServerClient).disconnect();
	}

	@Test
	public void lifecycleIsOperationalAfterSuccessfulProvisioning() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		connectionLifecycleManager.handleProvisioningInformation(mock(ProvisionEvent.class));

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.OPERATIONAL);
	}

	@Test
	public void notifiesAboutOperationalStateAfterSuccessfulProvisioning() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		verify(eventPublisher).publishEvent(any(AddressDiscoveryEvent.class));
		verify(eventPublisher).publishEvent(any(ConnectingEvent.class));
		verify(eventPublisher).publishEvent(any(AwaitingProvisioningEvent.class));

		connectionLifecycleManager.handleProvisioningInformation(mock(ProvisionEvent.class));

		verify(eventPublisher).publishEvent(any(OperationalEvent.class));

		verifyNoMoreInteractions(eventPublisher);
	}

	@Test
	public void restartsDiscoveryAfterTransportErrorWhileOperational() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();

		connectionLifecycleManager.handleProvisioningInformation(mock(ProvisionEvent.class));
		connectionLifecycleManager.handleTransportErrorEvent(mock(ControlServerSessionTransportErrorEvent.class));

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.DISCOVERY);
		verify(taskScheduler, times(2)).schedule(any(), any(Trigger.class));
		verify(controlServerClient).disconnect();
	}

	@Test
	public void notifiesAboutDeProvisioningWhileOperational() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		verify(eventPublisher).publishEvent(any(AddressDiscoveryEvent.class));
		verify(eventPublisher).publishEvent(any(ConnectingEvent.class));
		verify(eventPublisher).publishEvent(any(AwaitingProvisioningEvent.class));

		connectionLifecycleManager.handleProvisioningInformation(mock(ProvisionEvent.class));
		verify(eventPublisher).publishEvent(any(OperationalEvent.class));

		connectionLifecycleManager.handleTransportErrorEvent(mock(ControlServerSessionTransportErrorEvent.class));
		verify(eventPublisher).publishEvent(any(DeProvisionEvent.class));
		verify(eventPublisher).publishEvent(any(DisconnectedEvent.class));

		verifyNoMoreInteractions(eventPublisher);
	}

	@Test
	public void notifiesAboutDisconnection() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		verify(eventPublisher).publishEvent(any(AddressDiscoveryEvent.class));
		verify(eventPublisher).publishEvent(any(ConnectingEvent.class));
		verify(eventPublisher).publishEvent(any(AwaitingProvisioningEvent.class));

		connectionLifecycleManager.handleTransportErrorEvent(mock(ControlServerSessionTransportErrorEvent.class));
		verify(eventPublisher).publishEvent(any(DisconnectedEvent.class));

		verifyNoMoreInteractions(eventPublisher);
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
