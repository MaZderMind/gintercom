package de.mazdermind.gintercom.shared.controlserver;

import java.util.List;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("WeakerAccess")
public class TestClientConfiguration implements GintercomClientConfiguration {
	public static final String CLIENT_ID = "TEST:TEST";
	public static final String CLIENT_MODEL = "testclient";
	public static final int PROTOCOL_VERSION = 42;
	public static final ImmutableList<String> BUTTONS = ImmutableList.of("A", "B", "C");

	@Override
	public String getClientId() {
		return CLIENT_ID;
	}

	@Override
	public Integer getProtocolVersion() {
		return PROTOCOL_VERSION;
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
