package de.mazdermind.gintercom.shared.controlserver.messagehandler;

import org.springframework.messaging.simp.stomp.StompFrameHandler;

public interface MatrixMessageHandler extends StompFrameHandler {
	String getDestination();
}
