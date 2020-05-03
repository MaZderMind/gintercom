package de.mazdermind.gintercom.clientapi.messages.provision;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProvisionMessage {
	@NotNull
	@Valid
	private ProvisioningInformation provisioningInformation;
}
