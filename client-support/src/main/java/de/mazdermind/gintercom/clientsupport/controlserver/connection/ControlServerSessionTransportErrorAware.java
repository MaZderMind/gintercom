package de.mazdermind.gintercom.clientsupport.controlserver.connection;

public interface ControlServerSessionTransportErrorAware {
	void handleTransportErrorEvent(ControlServerSessionTransportErrorEvent transportErrorEvent);
}
