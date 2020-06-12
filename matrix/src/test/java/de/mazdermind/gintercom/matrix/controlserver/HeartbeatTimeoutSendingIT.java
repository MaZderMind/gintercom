package de.mazdermind.gintercom.matrix.controlserver;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.MatrixHeartbeatMessage;
import de.mazdermind.gintercom.matrix.ControlServerTestBase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatTimeoutSendingIT extends ControlServerTestBase {
	@Autowired
	private TimeoutManager timeoutManager;

	@Test
	public void sendsHeartbeatMessagesToAssociatedClient() {
		associateClient();

		timeoutManager.sendHeartbeatMessages();
		client.awaitMessage(MatrixHeartbeatMessage.class);

		timeoutManager.sendHeartbeatMessages();
		client.awaitMessage(MatrixHeartbeatMessage.class);
	}
}
