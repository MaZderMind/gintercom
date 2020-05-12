package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import static de.mazdermind.gintercom.matrix.frameworkconfig.IpAddressHandshakeInterceptor.IP_ADDRESS_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.google.common.collect.ImmutableMap;

import de.mazdermind.gintercom.clientapi.messages.provision.AlreadyRegisteredMessage;
import de.mazdermind.gintercom.clientapi.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.clientapi.messages.registration.PanelRegistrationMessage;
import de.mazdermind.gintercom.matrix.configuration.ButtonSetResolver;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.portpool.PortSet;

@RunWith(MockitoJUnitRunner.class)
public class PanelRegistrationControllerTest {
	private static final InetAddress IP_ADDRESS = mock(InetAddress.class);
	private static final String SESSION_ID = "THE_SESSION_ID";
	private static final String UNKNOWN_SESSION_ID = "THE_UNKNOWN_SESSION_ID";
	private static final String UNKNOWN_HOST_ID = "THE_UNKNOWN_HOST_ID";
	private static final String UNREGISTERED_HOST_ID = "THE_UNREGISTERED_HOST_ID";
	private static final String KNOWN_HOST_ID = "THE_KNOWN_HOST_ID";
	private static final String PANEL_ID = "THE_PANEL_ID";
	private static final String DISPLAY_NAME = "THS_DISPLAY_NAME";

	@Mock
	private Config config;

	@Mock
	private PortAllocationManager portAllocationManager;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private ButtonSetResolver buttonSetResolver;

	@Mock
	private PanelConnectionManager panelConnectionManager;

	@Mock
	private SimpReponder simpReponder;

	@Mock
	private SimpMessageHeaderAccessor headerAccessor;

	@Mock
	private SessionDisconnectEvent sessionDisconnectEvent;

	@InjectMocks
	private PanelRegistrationController registrationController;

	private PanelConnectionInformation connectionInformation;
	private PanelRegistrationMessage panelRegistrationMessage;

	@Before
	public void prepareMocks() {
		when(headerAccessor.getSessionId()).thenReturn(SESSION_ID);
		when(headerAccessor.getSessionAttributes()).thenReturn(ImmutableMap.of(
			IP_ADDRESS_ATTRIBUTE, IP_ADDRESS
		));

		panelRegistrationMessage = new PanelRegistrationMessage()
			.setHostId(KNOWN_HOST_ID);

		connectionInformation = new PanelConnectionInformation()
			.setConnectionTime(LocalDateTime.now())
			.setHostId(KNOWN_HOST_ID)
			.setRemoteIp(IP_ADDRESS)
			.setSessionId(SESSION_ID);

		when(config.findPanelIdForHostId(KNOWN_HOST_ID)).thenReturn(Optional.of(PANEL_ID));
		when(config.getPanels()).thenReturn(ImmutableMap.of(
			PANEL_ID, new PanelConfig()
				.setDisplay(DISPLAY_NAME)
				.setHostId(KNOWN_HOST_ID)
		));

		when(portAllocationManager.allocatePortSet(KNOWN_HOST_ID))
			.thenReturn(new PortSet(42, 23));

		when(sessionDisconnectEvent.getSessionId()).thenReturn(SESSION_ID);
	}

	@Test
	public void noResponseWhenHostIsUnknown() {
		panelRegistrationMessage.setHostId(UNKNOWN_HOST_ID);
		registrationController.handleRegistrationRequest(headerAccessor, panelRegistrationMessage);

		verify(simpReponder, never()).convertAndRespondToUser(anyString(), anyString(), any());
		verifyNoMoreInteractions(simpReponder);
	}

	@Test
	public void noEventsAreEmittedWhenHostIsUnknown() {
		panelRegistrationMessage.setHostId(UNKNOWN_HOST_ID);
		registrationController.handleRegistrationRequest(headerAccessor, panelRegistrationMessage);

		verify(eventPublisher, never()).publishEvent(any());
		verifyNoMoreInteractions(eventPublisher);
	}

	@Test
	public void respondsWithAlreadyRegisteredMessageWhenHostAlreadyConnected() {
		when(panelConnectionManager.getConnectionInformationForHostId(KNOWN_HOST_ID))
			.thenReturn(Optional.of(connectionInformation));

		registrationController.handleRegistrationRequest(headerAccessor, panelRegistrationMessage);

		ArgumentCaptor<AlreadyRegisteredMessage> captor = ArgumentCaptor.forClass(AlreadyRegisteredMessage.class);

		verify(simpReponder, times(1)).convertAndRespondToUser(
			eq(SESSION_ID), eq("/provision/already-registered"), captor.capture());
		assertThat(captor.getValue().getConnectionTime()).isEqualTo(connectionInformation.getConnectionTime());
		assertThat(captor.getValue().getRemoteIp()).isEqualTo(connectionInformation.getRemoteIp());

		verifyNoMoreInteractions(simpReponder);
	}

	@Test
	public void respondsWithProvisionMessageWhenHostIdIsKnown() {
		registrationController.handleRegistrationRequest(headerAccessor, panelRegistrationMessage);

		ArgumentCaptor<ProvisionMessage> captor = ArgumentCaptor.forClass(ProvisionMessage.class);
		verify(simpReponder, times(1)).convertAndRespondToUser(
			eq(SESSION_ID), eq("/provision"), captor.capture());
		assertThat(captor.getValue().getProvisioningInformation().getDisplay()).isEqualTo(DISPLAY_NAME);

		verifyNoMoreInteractions(simpReponder);
	}

	@Test
	public void emitsPanelRegistrationEventWhenHostIdIsKnown() {
		registrationController.handleRegistrationRequest(headerAccessor, panelRegistrationMessage);

		ArgumentCaptor<PanelRegistrationEvent> captor = ArgumentCaptor.forClass(PanelRegistrationEvent.class);
		verify(eventPublisher, times(1)).publishEvent(captor.capture());

		assertThat(captor.getValue().getPanelConfig().getHostId()).isEqualTo(KNOWN_HOST_ID);
		assertThat(captor.getValue().getPanelConfig().getDisplay()).isEqualTo(DISPLAY_NAME);

		assertThat(captor.getValue().getPanelId()).isEqualTo(PANEL_ID);
		assertThat(captor.getValue().getHostAddress()).isEqualTo(IP_ADDRESS);
	}

	@Test
	public void registersPanelWhenHostIdIsKnown() {
		registrationController.handleRegistrationRequest(headerAccessor, panelRegistrationMessage);

		verify(panelConnectionManager, times(1))
			.registerPanelConnection(eq(SESSION_ID), any(PanelConnectionInformation.class));
	}

	@Test
	public void respondsWithAlreadyRegisteredMessageWhenHostIsAlreadyRegisteredEvenWithUnknownHostId() {
		panelRegistrationMessage.setHostId(UNKNOWN_HOST_ID);
		when(panelConnectionManager.getConnectionInformationForHostId(UNKNOWN_HOST_ID))
			.thenReturn(Optional.of(connectionInformation));

		registrationController.handleRegistrationRequest(headerAccessor, panelRegistrationMessage);

		ArgumentCaptor<AlreadyRegisteredMessage> captor = ArgumentCaptor.forClass(AlreadyRegisteredMessage.class);

		verify(simpReponder, times(1)).convertAndRespondToUser(
			eq(SESSION_ID), eq("/provision/already-registered"), captor.capture());
		assertThat(captor.getValue().getConnectionTime()).isEqualTo(connectionInformation.getConnectionTime());
		assertThat(captor.getValue().getRemoteIp()).isEqualTo(connectionInformation.getRemoteIp());

		verifyNoMoreInteractions(simpReponder);
	}

	@Test
	public void doesNotEmitEventWhenHostIsAlreadyRegisteredEvenWithUnknownHostId() {
		panelRegistrationMessage.setHostId(UNKNOWN_HOST_ID);
		when(panelConnectionManager.getConnectionInformationForHostId(UNKNOWN_HOST_ID))
			.thenReturn(Optional.of(connectionInformation));

		registrationController.handleRegistrationRequest(headerAccessor, panelRegistrationMessage);

		verifyNoMoreInteractions(eventPublisher);
	}

	@Test
	public void disconnectWithUnregisteredSessionIdIsTolerated() {
		when(sessionDisconnectEvent.getSessionId()).thenReturn(UNKNOWN_SESSION_ID);
		registrationController.handlePanelDisconnect(sessionDisconnectEvent);

		verifyNoMoreInteractions(simpReponder);
		verifyNoMoreInteractions(eventPublisher);
	}

	@Test
	public void disconnectDeregistersPanel() {
		registrationController.handlePanelDisconnect(sessionDisconnectEvent);

		verify(panelConnectionManager, times(1)).deregisterPanelConnection(SESSION_ID);
	}

	@Test
	public void disconnectWithUnknownHostIdDoesNotEmitEvent() {
		connectionInformation.setHostId(UNKNOWN_HOST_ID);
		when(panelConnectionManager.deregisterPanelConnection(SESSION_ID)).thenReturn(Optional.of(connectionInformation));
		registrationController.handlePanelDisconnect(sessionDisconnectEvent);

		verifyNoMoreInteractions(eventPublisher);
	}

	@Test
	public void disconnectWithKnownHostIdDoesEmitPanelDeRegistrationEvent() {
		when(panelConnectionManager.deregisterPanelConnection(SESSION_ID)).thenReturn(Optional.of(connectionInformation));
		registrationController.handlePanelDisconnect(sessionDisconnectEvent);

		ArgumentCaptor<PanelDeRegistrationEvent> captor = ArgumentCaptor
			.forClass(PanelDeRegistrationEvent.class);
		verify(eventPublisher, times(1)).publishEvent(captor.capture());

		assertThat(captor.getValue().getPanelId()).isEqualTo(PANEL_ID);

		verifyNoMoreInteractions(eventPublisher);
	}
}
