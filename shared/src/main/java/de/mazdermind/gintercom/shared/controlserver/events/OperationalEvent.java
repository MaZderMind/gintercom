package de.mazdermind.gintercom.shared.controlserver.events;

import org.apache.commons.lang3.builder.ToStringBuilder;

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

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.toString();
	}
}
