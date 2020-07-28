package de.mazdermind.gintercom.matrix.controlserver;


import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.concurrent.TimeoutException;

import org.junit.Test;

import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ErrorMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import de.mazdermind.gintercom.matrix.tools.TestClientIdGenerator;


/**
 * see AssociationIT & ControlServerIT for positive Test-Cases
 */
public class ControlServerNetworkIT extends ControlServerTestBase {
	@Test
	public void rejectsEmptyMessage() throws TimeoutException, InterruptedException {
		client.transmit("\n");

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains("MalformedMessageException")
			.contains("No JSON found");
	}

	@Test
	public void rejectsGarbageMessage() throws TimeoutException, InterruptedException {
		client.transmit("lalafoo");

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains("JsonParseException")
			.contains("Unrecognized token 'lalafoo'");
	}

	@Test
	public void rejectsUnknownMessage() throws TimeoutException, InterruptedException {
		//language=JSON
		client.transmit("{\n" +
			"\t\"type\": \"UnknownMessage\"\n" +
			"}");

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains("MalformedMessageException")
			.contains("type-field does not name a valid Message");
	}

	@Test
	public void rejectsMessageWithoutAssociation() {
		//language=JSON
		client.transmit("{\n" +
			"\t\"type\": \"ClientHeartbeatMessage\"\n" +
			"}");

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains("The Socket-Address")
			.contains("is not not associated");
	}

	@Test
	public void rejectsInvalidMessage() {
		this.associateClient(TestClientIdGenerator.generateTestClientId());
		//language=JSON
		client.transmit("{\n" +
			"\t\"type\": \"DeAssociationRequestMessage\"\n" +
			"}");

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains("ConstraintViolationException");
	}

	@Test
	public void rejectsMatrixToClientMessage() throws TimeoutException, InterruptedException {
		//language=JSON
		client.transmit("{\n" +
			"\t\"type\": \"AssociatedMessage\"\n" +
			"}");

		ErrorMessage errorMessage = client.awaitMessage(ErrorMessage.class);
		assertThat(errorMessage.getMessage())
			.contains("MalformedMessageException")
			.contains("type-field does not name a valid Message");
	}
}
