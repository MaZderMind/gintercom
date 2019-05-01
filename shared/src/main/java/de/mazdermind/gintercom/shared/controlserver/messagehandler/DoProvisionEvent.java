package de.mazdermind.gintercom.shared.controlserver.messagehandler;

import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;

public class DoProvisionEvent {
	private final ProvisionMessage provisionMessage; // TODO Figure out a better Name -- we shouldn't pass a "Message" object around

	public DoProvisionEvent(ProvisionMessage provisionMessage) {
		this.provisionMessage = provisionMessage;
	}

	public ProvisionMessage getProvisionMessage() {
		return provisionMessage;
	}
}
