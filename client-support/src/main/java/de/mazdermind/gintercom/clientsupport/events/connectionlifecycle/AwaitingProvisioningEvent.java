package de.mazdermind.gintercom.clientsupport.events.connectionlifecycle;

import de.mazdermind.gintercom.clientsupport.connectionlifecycle.ConnectionLifecycle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AwaitingProvisioningEvent extends ConnectionLifecycleEvent {
	private String clientId;

	@Override
	public String getDisplayText() {
		return "Awaiting Provisioning";
	}

	@Override
	public String getDetailsText() {
		return "Client-Id: " + clientId;
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.PROVISIONING;
	}
}
