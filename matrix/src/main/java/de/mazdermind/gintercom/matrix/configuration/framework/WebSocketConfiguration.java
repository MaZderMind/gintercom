package de.mazdermind.gintercom.matrix.configuration.framework;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	private final IpAddressHandshakeInterceptor ipAddressHandshakeInterceptor;

	public WebSocketConfiguration(
		@Autowired IpAddressHandshakeInterceptor ipAddressHandshakeInterceptor
	) {
		this.ipAddressHandshakeInterceptor = ipAddressHandshakeInterceptor;
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
			.addEndpoint("/ws")
			.addInterceptors(ipAddressHandshakeInterceptor);
	}
}
