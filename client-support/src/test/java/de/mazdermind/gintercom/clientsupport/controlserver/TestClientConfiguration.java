package de.mazdermind.gintercom.clientsupport.controlserver;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.clientapi.configuration.ClientConfiguration;

@SuppressWarnings("WeakerAccess")
public class TestClientConfiguration implements ClientConfiguration {
	public static final String HOST_ID = "TEST:TEST";
	public static final String CLIENT_MODEL = "testclient";
	public static final int PROTOCOL_VERSION = 42;
	public static final ImmutableList<String> BUTTONS = ImmutableList.of("A", "B", "C");

	@Override
	public String getHostId() {
		return HOST_ID;
	}

	@Override
	public String getClientModel() {
		return CLIENT_MODEL;
	}

	@Override
	public List<String> getButtons() {
		return BUTTONS;
	}
}
