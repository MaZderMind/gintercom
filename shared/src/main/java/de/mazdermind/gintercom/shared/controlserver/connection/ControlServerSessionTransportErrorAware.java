package de.mazdermind.gintercom.shared.controlserver.connection;

public interface ControlServerSessionTransportErrorAware {
	void handleTransportErrorEvent(ControlServerSessionTransportErrorEvent transportErrorEvent);
}
