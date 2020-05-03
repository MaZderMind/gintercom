package de.mazdermind.gintercom.clientsupport.controlserver.provisioning;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;

public interface ProvisioningInformationAware {
	void handleProvisioningInformation(ProvisioningInformation provisioningInformation);
}
