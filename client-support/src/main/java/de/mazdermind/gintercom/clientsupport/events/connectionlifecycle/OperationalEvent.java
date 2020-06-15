package de.mazdermind.gintercom.clientsupport.events.connectionlifecycle;

import de.mazdermind.gintercom.clientsupport.connectionlifecycle.ConnectionLifecycle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class OperationalEvent extends ConnectionLifecycleEvent {
	@Override
	public String getDisplayText() {
		return "Operational";
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.OPERATIONAL;
	}
}
