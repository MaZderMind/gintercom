package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Service;

@Service
public class SimpReponder {
	private final SimpMessageSendingOperations simpMessageSendingOperations;

	public SimpReponder(
		@Autowired SimpMessageSendingOperations simpMessageSendingOperations
	) {
		this.simpMessageSendingOperations = simpMessageSendingOperations;
	}

	// Resembling org.springframework.messaging.simp.annotation.support.SendToMethodReturnValueHandler
	public void convertAndRespondeToUser(String user, String destination, Object payload) {
		this.simpMessageSendingOperations.convertAndSendToUser(user, destination, payload, createHeaders(user));
	}

	private Map<String, Object> createHeaders(String sessionId) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		headerAccessor.setSessionId(sessionId);
		headerAccessor.setLeaveMutable(true);
		return headerAccessor.getMessageHeaders();
	}
}
