package de.mazdermind.gintercom.clientsupport.events.connectionlifecycle;

import de.mazdermind.gintercom.clientsupport.connectionlifecycle.ConnectionLifecycle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AddressDiscoveryEvent extends ConnectionLifecycleEvent {
	private String implementationId;
	private String implementationName;

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
