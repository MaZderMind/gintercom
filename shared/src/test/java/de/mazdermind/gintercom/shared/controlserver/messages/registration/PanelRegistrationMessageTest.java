package de.mazdermind.gintercom.shared.controlserver.messages.registration;

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

public class PanelRegistrationMessageTest {

	private static final String HOST_ID = "DEAD:BEEF";
	private static final String CLIENT_MODEL = "test-client";
	private static final int PROTOCOL_VERSION = 42;
	private static final List<String> BUTTONS = ImmutableList.of("1", "2", "3", "4", "X1", "X2");

	private PanelRegistrationMessage panelRegistrationMessage;

	@Before
	public void generateTestdata() {
		panelRegistrationMessage = new PanelRegistrationMessage()
			.setHostId(HOST_ID)
			.setClientModel(CLIENT_MODEL)
			.setProtocolVersion(PROTOCOL_VERSION)
			.setCapabilities(new Capabilities()
				.setButtons(BUTTONS));
	}

	@Test
	public void serializesCorrectly() {
		JsonMap json = JsonMapUtils.convertToJson(panelRegistrationMessage);
		assertThat(json.get("hostId"), is(HOST_ID));
		assertThat(json.get("clientModel"), is(CLIENT_MODEL));
		assertThat(json.get("protocolVersion"), is(PROTOCOL_VERSION));
		assertThat(json.getObject("capabilities").get("buttons"), is(BUTTONS));
	}

	@Test
	public void deserializesCorrectly() {
		JsonMap json = JsonMapUtils.convertToJson(this.panelRegistrationMessage);
		PanelRegistrationMessage panelRegistrationMessage = JsonMapUtils.convertJsonTo(PanelRegistrationMessage.class, json);

		assertThat(panelRegistrationMessage.getHostId(), is(HOST_ID));
		assertThat(panelRegistrationMessage.getClientModel(), is(CLIENT_MODEL));
		assertThat(panelRegistrationMessage.getProtocolVersion(), is(PROTOCOL_VERSION));
		assertThat(panelRegistrationMessage.getCapabilities().getButtons(), is(BUTTONS));
	}

	@Test
	public void messageValidates() {
		assertThat(panelRegistrationMessage, validates());
	}

	@Test
	public void failsValidationWithoutHostId() {
		panelRegistrationMessage.setHostId(null);
		assertThat(panelRegistrationMessage, not(validates()));
	}

	@Test
	public void failsValidationWithoutProtocolVersion() {
		panelRegistrationMessage.setProtocolVersion(null);
		assertThat(panelRegistrationMessage, not(validates()));

	}

	@Test
	public void failsValidationWithoutClientModel() {
		panelRegistrationMessage.setClientModel(null);
		assertThat(panelRegistrationMessage, not(validates()));
	}

	@Test
	public void failsValidationWithoutCapabilities() {
		panelRegistrationMessage.setCapabilities(null);
		assertThat(panelRegistrationMessage, not(validates()));
	}

	@Test
	public void failsValidationWithoutButtons() {
		panelRegistrationMessage.getCapabilities().setButtons(emptyList());
		assertThat(panelRegistrationMessage, not(validates()));
	}
}
