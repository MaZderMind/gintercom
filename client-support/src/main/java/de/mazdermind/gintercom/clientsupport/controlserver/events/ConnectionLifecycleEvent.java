package de.mazdermind.gintercom.clientsupport.controlserver.events;

import de.mazdermind.gintercom.clientsupport.controlserver.ConnectionLifecycle;

/**
 * Event emitted by the ConnectionLifecycleManager to notify interested Components in the Client about Changes in the Connection between
 * Matrix and Client. The Interface of the {@link ConnectionLifecycleEvent} is tailored towards displaying Status-Information about the
 * state of the connection to a User using the {@link #getDisplayText()} and {@link #getDetailsText} methods. A decision whether the
 * Client is Operational (and should thus, for example, unlock the Control-UI) can be made by obtaining the Lifecycle Phase with
 * {@link #getLifecycle()} and then calling {@link ConnectionLifecycle#isOperational()} on it.
 */
public abstract class ConnectionLifecycleEvent {
	public abstract String getDisplayText();

	public String getDetailsText() {
		return "";
	}

	public abstract ConnectionLifecycle getLifecycle();
}
