package de.mazdermind.gintercom.clientsupport.controlserver.messagehandler;

import org.springframework.messaging.simp.stomp.StompFrameHandler;

public interface MatrixMessageHandler extends StompFrameHandler {
	String getDestination();
}
