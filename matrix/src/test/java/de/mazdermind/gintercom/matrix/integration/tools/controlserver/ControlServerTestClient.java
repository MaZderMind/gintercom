package de.mazdermind.gintercom.matrix.integration.tools.controlserver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

public class ControlServerTestClient {
	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(1);
	private static final String HOST = "127.0.0.1";
	private static final String PATH = "/ws";
	private static final Set<String> EXPECTED_MESSAGES = ImmutableSet.of("/user/provision");
	private static final Logger log = LoggerFactory.getLogger(ControlServerTestClient.class);
	private final ObjectMapper objectMapper;

	private final int serverPort;

	private StompSession stompSession;
	private WebSocketStompClient stompClient;
	private ControlServerTestSessionHandler sessionHandler;

	public ControlServerTestClient(int serverPort) {
		objectMapper = new ObjectMapper();
		this.serverPort = serverPort;
	}

	/**
	 * Connect to the ControlServer under Test
	 * Throw an exception if it fails
	 * <p>
	 * usually called inside the @Test
	 */
	public void connect() {
		connect(DEFAULT_TIMEOUT);
	}

	/**
	 * Connect to the ControlServer under Test
	 * Throw an exception if it fails
	 * <p>
	 * usually called inside the @Test
	 */
	public void connect(Duration timeout) {
		if (stompSession != null) {
			throw new AssertionError("Another StompSession is already active. Ensure to call cleanup() after your Tests");
		}

		URI websocketUri;
		try {
			websocketUri = new URI("ws", null, HOST, serverPort, PATH, null, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		log.info("Connecting to Websocket-Server at {}", websocketUri);
		stompClient = new WebSocketStompClient(new StandardWebSocketClient());
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		try {
			sessionHandler = new ControlServerTestSessionHandler();
			stompSession = stompClient.connect(websocketUri.toString(), sessionHandler)
				.get(timeout.toMillis(), TimeUnit.MILLISECONDS);

			EXPECTED_MESSAGES.forEach(destination -> {
				log.info("Subscribing to {}", destination);
				stompSession.subscribe(destination, sessionHandler);
			});
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.info("Failed to Connect to Websocket-Server: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Disconnect from the ControlServer under Test
	 * Throw an Exception if the TestClient was not connected or the disconnection fails
	 * <p>
	 * usually called inside the @Test
	 */
	public void disconnect() {
		log.info("Disconnecting from Websocket");
		if (stompSession == null) {
			throw new AssertionError("No active StompSession");
		}
		if (!stompSession.isConnected()) {
			throw new AssertionError("StompSession was not connected");
		}
		stompSession.disconnect();
		stompSession = null;

		if (stompClient == null) {
			throw new AssertionError("No active StompClient");
		}
		if (stompClient.isRunning()) {
			stompClient.stop();
		}
		stompClient = null;
		log.info("Successfully Disconnected from Websocket");
	}

	/**
	 * Do whatever is necessary to cleanup a possibly open connection.
	 * Throw an Exception if the disconnection fails
	 * <p>
	 * usually called inside a @After method
	 */
	public void cleanup() {
		try {
			if (stompSession != null && stompSession.isConnected()) {
				log.warn("Cleaning Up still connected Stomp-Session");
				stompSession.disconnect();
			}
		} catch (Exception e) {
			log.warn("Caught Exception while cleaning up", e);
		} finally {
			stompSession = null;
		}

		try {
			if (stompClient != null && stompClient.isRunning()) {
				log.warn("Cleaning Up still running Stomp-Client");
				stompClient.stop();
			}
		} catch (Exception e) {
			log.warn("Caught Exception while cleaning up", e);
		} finally {
			stompClient = null;
		}

		sessionHandler = null;
	}

	/**
	 * Send a Message {@param message} to {@param destination} on the ControlServer and return when it has been sent.
	 * Throw an Exception if the sending fails
	 */
	public void send(String destination, Object message) {
		log.warn("Sending Message to {}: \n{}", destination, message.toString());
		stompSession.send(destination, message);
	}

	/**
	 * Wait 1 Second for Messages and return the received Messages.
	 * If you expect a specific message, better use {@link #awaitMessage(String, Duration)} or
	 * {@link #awaitMessage(String, Class, Duration)}, because the will return as soon as such a message
	 * has been received.
	 * <p>
	 * Clears the internal List of received Messages after returning it, so that calling {@link #assertNoOtherMessages()}
	 * will not raise an Assertation-Exception.
	 *
	 * @return The List of received Messages or an empty List, if none has been received.
	 */
	public List<StompMessage> awaitMessages() {
		return awaitMessages(DEFAULT_TIMEOUT);
	}

	/**
	 * Wait {@param duration} for Messages and return the received Messages.
	 * If you expect a specific message, better use {@link #awaitMessage(String, Duration)} or
	 * {@link #awaitMessage(String, Class, Duration)}, because the will return as soon as such a message
	 * has been received.
	 * <p>
	 * Clears the internal List of received Messages after returning it, so that calling {@link #assertNoOtherMessages()}
	 * will not raise an Assertation-Exception.
	 *
	 * @return The List of received Messages or an empty List, if none has been received.
	 */
	public List<StompMessage> awaitMessages(Duration duration) {
		log.warn("Waiting {} for Messages", duration);
		try {
			Thread.sleep(duration.toMillis());
		} catch (InterruptedException e) {
			log.warn("Waiting for Messages was Interrupted");
			throw new RuntimeException(e);
		}

		List<StompMessage> messages = sessionHandler.getMessages();
		sessionHandler.getMessages().clear();
		log.info("Received {} Messages", messages.size());
		return messages;
	}

	/**
	 * Wait up to 1 Second for a Message received on {@param destination} and try to decode it as
	 * {@param klazz}.
	 */
	public <T> T awaitMessage(String destination, Class<T> klazz) {
		return awaitMessage(destination, klazz, DEFAULT_TIMEOUT);
	}

	/**
	 * Wait up to {@param timeout} for a Message received on {@param destination} and try to decode it as
	 * {@param klazz}.
	 */
	public <T> T awaitMessage(String destination, Class<T> klazz, Duration timeout) {
		Object message = awaitMessage(destination, timeout);
		return objectMapper.convertValue(message, klazz);
	}

	public Object awaitMessage(String destination) {
		return awaitMessage(destination, DEFAULT_TIMEOUT);
	}

	public Object awaitMessage(String destination, Duration timeout) {
		return sessionHandler.awaitMessage(destination, timeout);
	}

	public void assertNoOtherMessages() {
		assertThat(sessionHandler.getMessages(), empty());
	}

	public void assertNoErrors() {
		assertThat(sessionHandler.getErrors(), empty());
	}
}
