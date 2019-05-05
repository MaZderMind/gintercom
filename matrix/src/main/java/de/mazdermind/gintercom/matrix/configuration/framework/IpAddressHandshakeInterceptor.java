package de.mazdermind.gintercom.matrix.configuration.framework;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class IpAddressHandshakeInterceptor implements HandshakeInterceptor {
	public static final String IP_ADDRESS_ATTRIBUTR = "IP_ADDRESS";

	@Override
	public boolean beforeHandshake(
		ServerHttpRequest request,
		ServerHttpResponse response,
		WebSocketHandler webSocketHandler,
		Map<String, Object> attributes
	) {
		// Bind IP attribute to WebSocket session
		attributes.put(IP_ADDRESS_ATTRIBUTR, request.getRemoteAddress().getAddress());

		return true;
	}

	@Override
	public void afterHandshake(
		ServerHttpRequest request,
		ServerHttpResponse response,
		WebSocketHandler webSocketHandler,
		Exception e
	) {
	}
}
