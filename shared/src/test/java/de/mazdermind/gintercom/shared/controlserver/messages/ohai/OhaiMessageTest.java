package de.mazdermind.gintercom.shared.controlserver.messages.ohai;

import static de.mazdermind.gintercom.testutils.matchers.ValidatesMatcher.validates;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.testutils.JsonMap;
import de.mazdermind.gintercom.testutils.JsonMapUtils;

public class OhaiMessageTest {

	private static final String CLIENT_ID = "DEAD:BEEF";
	private static final String CLIENT_MODEL = "test-client";
	private static final int PROTOCOL_VERSION = 42;
	private static final List<String> BUTTONS = ImmutableList.of("1", "2", "3", "4", "X1", "X2");

	private OhaiMessage ohaiMessage;

	@Before
	public void generateTestdata() {
		ohaiMessage = new OhaiMessage()
			.setClientId(CLIENT_ID)
			.setClientModel(CLIENT_MODEL)
			.setProtocolVersion(PROTOCOL_VERSION)
			.setCapabilities(new Capabilities()
				.setButtons(BUTTONS));
	}

	@Test
	public void serializesCorrectly() {
		JsonMap json = JsonMapUtils.convertToJson(ohaiMessage);
		assertThat(json.get("clientId"), is(CLIENT_ID));
		assertThat(json.get("clientModel"), is(CLIENT_MODEL));
		assertThat(json.get("protocolVersion"), is(PROTOCOL_VERSION));
		assertThat(json.getObject("capabilities").get("buttons"), is(BUTTONS));
	}

	@Test
	public void deserializesCorrectly() {
		JsonMap json = JsonMapUtils.convertToJson(ohaiMessage);
		OhaiMessage ohaiMessage = JsonMapUtils.convertJsonTo(OhaiMessage.class, json);

		assertThat(ohaiMessage.getClientId(), is(CLIENT_ID));
		assertThat(ohaiMessage.getClientModel(), is(CLIENT_MODEL));
		assertThat(ohaiMessage.getProtocolVersion(), is(PROTOCOL_VERSION));
		assertThat(ohaiMessage.getCapabilities().getButtons(), is(BUTTONS));
	}

	@Test
	public void messageValidates() {
		assertThat(ohaiMessage, validates());
	}

	@Test
	public void failsValidationWithoutClientId() {
		ohaiMessage.setClientId(null);
		assertThat(ohaiMessage, not(validates()));
	}

	@Test
	public void failsValidationWithoutProtocolVersion() {
		ohaiMessage.setProtocolVersion(null);
		assertThat(ohaiMessage, not(validates()));

	}

	@Test
	public void failsValidationWithoutClientModel() {
		ohaiMessage.setClientModel(null);
		assertThat(ohaiMessage, not(validates()));
	}

	@Test
	public void failsValidationWithoutCapabilities() {
		ohaiMessage.setCapabilities(null);
		assertThat(ohaiMessage, not(validates()));
	}

	@Test
	public void failsValidationWithoutButtons() {
		ohaiMessage.getCapabilities().setButtons(emptyList());
		assertThat(ohaiMessage, not(validates()));
	}
}
