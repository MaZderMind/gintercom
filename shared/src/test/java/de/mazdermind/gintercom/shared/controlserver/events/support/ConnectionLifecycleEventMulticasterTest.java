package de.mazdermind.gintercom.shared.controlserver.events.support;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.shared.controlserver.events.AddressDiscoveryEvent;
import de.mazdermind.gintercom.shared.controlserver.events.AwaitingProvisioningEvent;
import de.mazdermind.gintercom.shared.controlserver.events.ConnectingEvent;
import de.mazdermind.gintercom.shared.controlserver.events.OperationalEvent;

public class ConnectionLifecycleEventMulticasterTest {

	private ConnectionLifecycleEventMulticaster multicaster;
	private ConnectionLifecycleEventAware aware;

	@Before
	public void prepare() {
		aware = mock(ConnectionLifecycleEventAware.class);
		multicaster = new ConnectionLifecycleEventMulticaster(ImmutableList.of(aware));
	}

	@After
	public void noMoreInteractions() {
		verifyNoMoreInteractions(aware);
	}

	@Test
	public void multicastAddressDiscoveryEvent() {
		AddressDiscoveryEvent event = mock(AddressDiscoveryEvent.class);
		multicaster.dispatch(event);
		verify(aware, times(1)).handleGenericConnectionLifecycleEvent(event);
		verify(aware, times(1)).handleAddressDiscoveryEvent(event);
	}

	@Test
	public void multicastAwaitingProvisioningEvent() {
		AwaitingProvisioningEvent event = mock(AwaitingProvisioningEvent.class);
		multicaster.dispatch(event);
		verify(aware, times(1)).handleGenericConnectionLifecycleEvent(event);
		verify(aware, times(1)).handleAwaitingProvisioningEvent(event);
	}

	@Test
	public void multicastConnectingEvent() {
		ConnectingEvent event = mock(ConnectingEvent.class);
		multicaster.dispatch(event);
		verify(aware, times(1)).handleGenericConnectionLifecycleEvent(event);
		verify(aware, times(1)).handleConnectingEvent(event);
	}

	@Test
	public void multicastOperationalEvent() {
		OperationalEvent event = mock(OperationalEvent.class);
		multicaster.dispatch(event);
		verify(aware, times(1)).handleGenericConnectionLifecycleEvent(event);
		verify(aware, times(1)).handleOperationalEvent(event);
	}

}
