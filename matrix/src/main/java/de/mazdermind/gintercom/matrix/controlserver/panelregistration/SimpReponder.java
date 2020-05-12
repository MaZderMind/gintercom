package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SimpReponder {
	private final SimpMessageSendingOperations simpMessageSendingOperations;

	// Resembling org.springframework.messaging.simp.annotation.support.SendToMethodReturnValueHandler
	public void convertAndRespondToUser(String user, String destination, Object payload) {
		this.simpMessageSendingOperations.convertAndSendToUser(user, destination, payload, createHeaders(user));
	}

	private Map<String, Object> createHeaders(String sessionId) {
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		headerAccessor.setSessionId(sessionId);
		headerAccessor.setLeaveMutable(true);
		return headerAccessor.getMessageHeaders();
	}
}
