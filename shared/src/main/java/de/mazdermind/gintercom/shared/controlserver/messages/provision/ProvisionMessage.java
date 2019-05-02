package de.mazdermind.gintercom.shared.controlserver.messages.provision;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.mazdermind.gintercom.shared.controlserver.provisioning.ProvisioningInformation;

public class ProvisionMessage {
	@NotNull
	@Valid
	private ProvisioningInformation provisioningInformation;

	public ProvisioningInformation getProvisioningInformation() {
		return provisioningInformation;
	}

	public ProvisionMessage setProvisioningInformation(ProvisioningInformation provisioningInformation) {
		this.provisioningInformation = provisioningInformation;
		return this;
	}
}
