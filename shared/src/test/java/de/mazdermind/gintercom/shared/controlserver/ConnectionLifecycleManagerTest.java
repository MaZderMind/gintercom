package de.mazdermind.gintercom.shared.controlserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import de.mazdermind.gintercom.shared.controlserver.connection.ControlServerClient;
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

public class ConnectionLifecycleManagerTest {

	private ConnectionLifecycleEventMulticaster connectionLifecycleEventMulticaster;
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
		when(taskScheduler.schedule(any(), any(Trigger.class))).thenReturn(scheduledDiscovery);

		matrixAddressDiscoveryServiceImplementation = mock(MatrixAddressDiscoveryServiceImplementation.class);
		when(matrixAddressDiscoveryServiceImplementation.getDisplayName()).thenReturn("Test-Discovery-Method");
		when(matrixAddressDiscoveryServiceImplementation.tryDiscovery()).thenReturn(Optional.empty());

		matrixAddressDiscoveryService = mock(MatrixAddressDiscoveryService.class);
		when(matrixAddressDiscoveryService.getNextImplementation())
			.thenReturn(matrixAddressDiscoveryServiceImplementation);

		successfulDiscovery = mock(MatrixAddressDiscoveryServiceResult.class);
		when(successfulDiscovery.getAddress()).thenReturn(InetAddress.getByName("10.56.23.42"));
		when(successfulDiscovery.getPort()).thenReturn(2342);

		controlServerClient = mock(ControlServerClient.class);
		when(controlServerClient.connect(any(InetAddress.class), anyInt())).thenReturn(Optional.empty());

		connectionLifecycleEventMulticaster = mock(ConnectionLifecycleEventMulticaster.class);

		stompSession = mock(StompSession.class);

		connectionLifecycleManager = new ConnectionLifecycleManager(
			connectionLifecycleEventMulticaster,
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
		verify(taskScheduler, times(1)).schedule(any(), any(Trigger.class));

		connectionLifecycleManager.discoveryTryNext();
		verify(matrixAddressDiscoveryService, times(1)).getNextImplementation();
		verify(matrixAddressDiscoveryServiceImplementation, times(1)).tryDiscovery();
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
		verify(scheduledDiscovery, never()).cancel(anyBoolean());
	}

	@Test
	public void notifiesAboutDiscoveryAttempt() {
		connectionLifecycleManager.discoveryTryNext();
		ArgumentCaptor<AddressDiscoveryEvent> captor = ArgumentCaptor.forClass(AddressDiscoveryEvent.class);
		verify(connectionLifecycleEventMulticaster, times(1)).dispatch(captor.capture());
		assertThat(captor.getValue().getImplementationName()).isEqualTo("Test-Discovery-Method");
	}

	@Test
	public void triesConnectionAfterSuccessfulDiscovery() {
		setupSuccessfulDiscovery();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		verify(controlServerClient, times(1)).connect(successfulDiscovery.getAddress(), successfulDiscovery.getPort());
	}

	@Test
	public void cancelsDiscoveryAfterSuccessfulDiscovery() {
		setupSuccessfulDiscovery();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		verify(scheduledDiscovery, times(1)).cancel(anyBoolean());
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

		when(controlServerClient.connect(successfulDiscovery.getAddress(), successfulDiscovery.getPort()))
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
		verify(connectionLifecycleEventMulticaster, times(1)).dispatch(captor.capture());
		assertThat(captor.getValue().getAddress()).isEqualTo(successfulDiscovery.getAddress());
		assertThat(captor.getValue().getPort()).isEqualTo(successfulDiscovery.getPort());
	}

	@Test
	public void initiatesProvisioningAfterSuccessfulConnection() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();

		ArgumentCaptor<PanelRegistrationMessage> captor = ArgumentCaptor.forClass(PanelRegistrationMessage.class);
		verify(stompSession).send(eq("/registration"), captor.capture());
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
		verify(connectionLifecycleEventMulticaster).dispatch(captor.capture());
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
		connectionLifecycleManager.handleTransportErrorEvent(mock(ControlServerSessionTransportErrorEvent.class));

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.DISCOVERY);
		verify(taskScheduler, times(2)).schedule(any(), any(Trigger.class));
		verify(controlServerClient, times(1)).disconnect();
	}

	@Test
	public void lifecycleIsOperationalAfterSuccessfulProvisioning() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		connectionLifecycleManager.handleProvisioningInformation(mock(ProvisioningInformation.class));

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.OPERATIONAL);
	}

	@Test
	public void notifiesAboutOperationalStateAfterSuccessfulProvisioning() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		connectionLifecycleManager.handleProvisioningInformation(mock(ProvisioningInformation.class));

		verify(connectionLifecycleEventMulticaster, times(1)).dispatch(any(OperationalEvent.class));
	}

	@Test
	public void restartsDiscoveryAfterTransportErrorWhileOperational() {
		setupSuccessfulConnection();

		connectionLifecycleManager.initiateDiscovery();
		connectionLifecycleManager.discoveryTryNext();
		connectionLifecycleManager.handleProvisioningInformation(mock(ProvisioningInformation.class));
		connectionLifecycleManager.handleTransportErrorEvent(mock(ControlServerSessionTransportErrorEvent.class));

		assertThat(connectionLifecycleManager.getLifecycle()).isEqualTo(ConnectionLifecycle.DISCOVERY);
		verify(taskScheduler, times(2)).schedule(any(), any(Trigger.class));
		verify(controlServerClient, times(1)).disconnect();
	}

	private void setupSuccessfulDiscovery() {
		when(matrixAddressDiscoveryServiceImplementation.tryDiscovery())
			.thenReturn(Optional.of(successfulDiscovery));
	}

	private void setupUnsuccessfulConnection() {
		setupSuccessfulDiscovery();
		when(controlServerClient.connect(successfulDiscovery.getAddress(), successfulDiscovery.getPort()))
			.thenReturn(Optional.empty());
	}

	private void setupSuccessfulConnection() {
		setupSuccessfulDiscovery();
		when(controlServerClient.connect(successfulDiscovery.getAddress(), successfulDiscovery.getPort()))
			.thenReturn(Optional.of(stompSession));
	}
}
