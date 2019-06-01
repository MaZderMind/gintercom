package de.mazdermind.gintercom.matrix.integration.tools.controlserver;

public class StompMessage {
	private String destination;
	private Object payload;

	public StompMessage(String destination, Object payload) {
		this.destination = destination;
		this.payload = payload;
	}

	public String getDestination() {
		return destination;
	}

	public Object getPayload() {
		return payload;
	}
}
