package de.mazdermind.gintercom.shared.controlserver;

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

	public void connect() {
		log.info("connecting to Server");
		StandardWebSocketClient client = new StandardWebSocketClient();

		stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		stompClient.connect("ws://localhost:8080/ws", sessionHandler);
	}

	@PreDestroy
	public void disconnect() {
		if (stompClient != null) {
			log.info("Stopping");
			stompClient.stop();
		}
	}
}
