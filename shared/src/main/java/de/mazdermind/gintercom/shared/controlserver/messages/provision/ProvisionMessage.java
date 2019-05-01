package de.mazdermind.gintercom.shared.controlserver.messages.provision;

import javax.validation.constraints.NotNull;

public class ProvisionMessage {
	@NotNull
	private String display;

	public String getDisplay() {
		return display;
	}

	public ProvisionMessage setDisplay(String display) {
		this.display = display;
		return this;
	}
}
