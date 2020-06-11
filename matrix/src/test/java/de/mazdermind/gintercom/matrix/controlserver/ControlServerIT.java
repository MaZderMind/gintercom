package de.mazdermind.gintercom.matrix.controlserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.ExampleMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ExampleResponseMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;

public class ControlServerIT extends ControlServerTestBase {
	@Autowired
	private MessageSender messageSender;

	@Before
	public void before() {
		associateClient();
	}

	@Test
	public void clientToMatrixMessage() {
		client.transmit(new ExampleMessage().setText("Hello World"));
		ExampleMessage.ClientMessage receivedEvent = eventReceiver.awaitEvent(ExampleMessage.ClientMessage.class);

		assertThat(receivedEvent.getHostId()).isEqualTo(HOST_ID);
		assertThat(receivedEvent.getMessage().getText()).isEqualTo("Hello World");
	}

	@Test
	public void matrixToClientMessage() {
		messageSender.sendMessageTo(HOST_ID, new ExampleResponseMessage().setText("Bye World"));

		ExampleResponseMessage responseMessage = client.awaitMessage(ExampleResponseMessage.class);
		assertThat(responseMessage.getText()).isEqualTo("Bye World");
	}
}
