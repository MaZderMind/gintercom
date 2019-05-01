package de.mazdermind.gintercom.shared.controlserver.connection;

public class ControlServerSessionTransportErrorEvent {
	private final String message;

	public ControlServerSessionTransportErrorEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
