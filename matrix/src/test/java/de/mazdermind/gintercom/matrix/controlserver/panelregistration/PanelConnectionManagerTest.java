package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

public class PanelConnectionManagerTest {

	public static final String HOST_ADDRESS = "10.220.72.0";
	public static final LocalDateTime CONNECTION_TIME = LocalDateTime.of(2010, 5, 1, 10, 0, 0);
	private static final String SESSION_ID = "SESSION_ID";
	private static final String OTHER_SESSION_ID = "OTHER_SESSION_ID";
	private static final String HOST_ID = "HOST_ID";
	private PanelConnectionManager panelConnectionManager;
	private PanelConnectionInformation connectionInformation;

	@Before
	public void setUp() throws UnknownHostException {
		panelConnectionManager = new PanelConnectionManager();
		connectionInformation = new PanelConnectionInformation()
			.setHostId(HOST_ID)
			.setRemoteIp(InetAddress.getByName(HOST_ADDRESS))
			.setConnectionTime(CONNECTION_TIME);
	}

	@Test
	public void returnsExpectedValuesWhenUnregistered() {
		assertThat(panelConnectionManager.getHostIdForSessionId(SESSION_ID), nullValue());
		assertThat(panelConnectionManager.getSessionIdForHostId(HOST_ID), nullValue());
		assertThat(panelConnectionManager.isHostIdAlreadyRegistered(HOST_ID), is(false));
	}

	@Test
	public void returnsExpectedValuesUnregistered() {
		panelConnectionManager.registerPanelConnection(SESSION_ID, connectionInformation);

		PanelConnectionInformation hostIdForSessionId = panelConnectionManager.getHostIdForSessionId(SESSION_ID);
		assertThat(hostIdForSessionId, notNullValue());
		assertThat(hostIdForSessionId.getHostId(), is(HOST_ID));
		assertThat(hostIdForSessionId.getRemoteIp().getHostAddress(), equalTo(HOST_ADDRESS));
		assertThat(hostIdForSessionId.getConnectionTime(), equalTo(CONNECTION_TIME));

		assertThat(panelConnectionManager.getSessionIdForHostId(HOST_ID), is(SESSION_ID));
		assertThat(panelConnectionManager.isHostIdAlreadyRegistered(HOST_ID), is(true));
	}

	@Test
	public void returnsExpectedValuesWhenDeRegistered() {
		panelConnectionManager.registerPanelConnection(SESSION_ID, connectionInformation);
		panelConnectionManager.deregisterPanelConnection(SESSION_ID);

		assertThat(panelConnectionManager.getHostIdForSessionId(SESSION_ID), nullValue());
		assertThat(panelConnectionManager.getSessionIdForHostId(HOST_ID), nullValue());
		assertThat(panelConnectionManager.isHostIdAlreadyRegistered(HOST_ID), is(false));
	}

	@Test(expected = IllegalArgumentException.class)
	public void doubleRegistrationIsNotPossible() {
		panelConnectionManager.registerPanelConnection(SESSION_ID, connectionInformation);
		panelConnectionManager.registerPanelConnection(OTHER_SESSION_ID, connectionInformation);
	}
}
