package de.mazdermind.gintercom.clientsupport.controlserver.events.connectionlifecycle;

import de.mazdermind.gintercom.clientsupport.controlserver.ConnectionLifecycle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AddressDiscoveryEvent extends ConnectionLifecycleEvent {
	private final String implementationId;
	private final String implementationName;

	@Override
	public String getDisplayText() {
		return "Searching for Matrix";
	}

	@Override
	public String getDetailsText() {
		return "using " + implementationName + "…";
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.DISCOVERY;
	}
}
