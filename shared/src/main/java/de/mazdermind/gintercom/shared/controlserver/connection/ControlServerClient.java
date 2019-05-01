package de.mazdermind.gintercom.shared.controlserver.connection;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Component
@Lazy
public class ControlServerClient {
	private static Logger log = LoggerFactory.getLogger(ControlServerClient.class);
	private final ControlServerSessionHandler sessionHandler;
	private WebSocketStompClient stompClient;

	public ControlServerClient(
		@Autowired ControlServerSessionHandler sessionHandler
	) {
		this.sessionHandler = sessionHandler;
		log.info("Created");
	}

	public void connect(InetAddress address, int port) {
		URI websocketUri = null;
		try {
			websocketUri = new URI("ws", null, address.getHostAddress(), port, "/ws", null, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e); // TODO decide what to do
		}
		log.info("connecting to Websocket-Server at {}", websocketUri);
		StandardWebSocketClient client = new StandardWebSocketClient();

		stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		stompClient.connect(websocketUri.toString(), sessionHandler);
	}

	@PreDestroy
	public void disconnect() {
		if (stompClient != null) {
			log.info("Stopping");
			stompClient.stop();
		}
	}
}
