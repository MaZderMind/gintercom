package de.mazdermind.gintercom.controlserver.shared.messages.client.to.matrix;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociateMessage;
import de.mazdermind.gintercom.testutils.assertations.IsValidCondition;

public class AssociateMessageTest {

	private AssociateMessage message;

	@Before
	public void before() {
		message = new AssociateMessage().setHostId("0000-0000")
			.setCapabilities(new AssociateMessage.Capabilities()
				.setButtons(ImmutableList.of("A1", "A2", "B1", "B2")));
	}

	@Test
	public void doesValidate() {
		assertThat(message).is(IsValidCondition.VALID);
	}

	@Test
	public void doesNotValidateWithoutHostId() {
		message.setHostId(null);
		assertThat(message).isNot(IsValidCondition.VALID);
	}

	@Test
	public void doesNotValidateWithoutCapabilities() {
		message.setCapabilities(null);
		assertThat(message).isNot(IsValidCondition.VALID);
	}

	@Test
	public void doesNotValidateWithInvalidCapabilities() {
		message.getCapabilities().setButtons(null);
		assertThat(message).isNot(IsValidCondition.VALID);
	}

	@Test
	public void capabilitiesCanBeExtendedInTheFuture() throws IOException {
		//language=JSON
		String capabilitiesWithExtraField = "{\n" +
			"\"buttons\":  [\"A\", \"B\", \"B\"],\n" +
			"\"extraField\": 42" +
			"}";

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.readValue(capabilitiesWithExtraField, AssociateMessage.Capabilities.class);
	}
}
