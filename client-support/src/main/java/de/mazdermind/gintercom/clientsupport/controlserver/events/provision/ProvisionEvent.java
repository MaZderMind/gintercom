package de.mazdermind.gintercom.clientsupport.controlserver.events.provision;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProvisionEvent {
	private final ProvisioningInformation provisioningInformation;
	private final MatrixAddressDiscoveryServiceResult matrixAddress;
}
