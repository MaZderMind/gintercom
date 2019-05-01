package de.mazdermind.gintercom.shared.controlserver.events;

import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycle;

public class OperationalEvent implements ConnectionLifecycleEvent {
	@Override
	public String getDisplayText() {
		return "Operational";
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.OPERATIONAL;
	}
}
