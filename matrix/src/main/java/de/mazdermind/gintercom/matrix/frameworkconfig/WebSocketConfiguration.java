package de.mazdermind.gintercom.matrix.frameworkconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	private final IpAddressHandshakeInterceptor ipAddressHandshakeInterceptor;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
			.addEndpoint("/ws")
			.addInterceptors(ipAddressHandshakeInterceptor);
	}
}
