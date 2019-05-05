package de.mazdermind.gintercom.shared.controlserver.provisioning;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProvisioningInformation {
	@NotNull
	private String display;

	public String getDisplay() {
		return display;
	}

	public ProvisioningInformation setDisplay(String display) {
		this.display = display;
		return this;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("display", display)
			.toString();
	}
}
