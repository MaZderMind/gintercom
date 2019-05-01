package de.mazdermind.gintercom.shared.controlserver.events;

import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycle;

public interface MatrixConnectionLifecycleEvent {
	String getDisplayText();

	ConnectionLifecycle getLifecycle();
}
