package de.mazdermind.gintercom.clientapi.messages.provision;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hashCode(provisioningInformation);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProvisionMessage that = (ProvisionMessage) o;
		return Objects.equal(provisioningInformation, that.provisioningInformation);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("provisioningInformation", provisioningInformation)
			.toString();
	}
}
