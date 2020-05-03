package de.mazdermind.gintercom.clientsupport.controlserver.connection;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ControlServerClient {
	private static final int CONNECTION_TIMEOUT_SECONDS = 5;

	private final ControlServerSessionHandler sessionHandler;
	private WebSocketStompClient stompClient;
	private StompSession stompSession;

	public ControlServerClient(
		@Autowired ControlServerSessionHandler sessionHandler
	) {
		this.sessionHandler = sessionHandler;
		log.info("Created");
	}

	public Optional<StompSession> connect(InetAddress address, int port) {
		URI websocketUri = null;
		try {
			websocketUri = new URI("ws", null, address.getHostAddress(), port, "/ws", null, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e); // TODO decide what to do
		}

		log.info("Connecting to Websocket-Server at {}", websocketUri);
		StandardWebSocketClient client = new StandardWebSocketClient();

		stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		ListenableFuture<StompSession> listenableFuture = stompClient.connect(websocketUri.toString(), sessionHandler);
		try {
			stompSession = listenableFuture.get(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
			log.info("Connected to Websocket-Server at {}", websocketUri);
			return Optional.of(stompSession);
		} catch (InterruptedException e) {
			log.info("Connection Interrupted");
			stompClient.stop();
			return Optional.empty();
		} catch (ExecutionException e) {
			e.printStackTrace();
			log.info("Connection Failed");
			stompClient.stop();
			return Optional.empty();
		} catch (TimeoutException e) {
			log.info("Connection Timed Out");
			stompClient.stop();
			return Optional.empty();
		}
	}

	@PreDestroy
	public void disconnect() {
		if (stompSession != null && stompSession.isConnected()) {
			log.info("Disconnecting");
			stompSession.disconnect();
		}

		if (stompClient != null && stompClient.isRunning()) {
			log.info("Stopping Client");
			stompClient.stop();
		}
	}
}
