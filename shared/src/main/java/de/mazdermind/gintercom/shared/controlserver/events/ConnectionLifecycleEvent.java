package de.mazdermind.gintercom.shared.controlserver.events;

import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycle;

public interface ConnectionLifecycleEvent {
	String getDisplayText();

	default String getDetailsText() {
		return "";
	}

	ConnectionLifecycle getLifecycle();
}
