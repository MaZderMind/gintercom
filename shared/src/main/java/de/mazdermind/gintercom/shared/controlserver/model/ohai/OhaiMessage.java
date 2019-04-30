package de.mazdermind.gintercom.shared.controlserver.model.ohai;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public class OhaiMessage {
	@NotNull
	private String clientId;

	@NotNull
	private String protocolVersion = "1.0";

	@NotNull
	private String clientModel;

	@Valid
	private Capabilities capabilities;

	public String getClientId() {
		return clientId;
	}

	public OhaiMessage setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public OhaiMessage setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
		return this;
	}

	public String getClientModel() {
		return clientModel;
	}

	public OhaiMessage setClientModel(String clientModel) {
		this.clientModel = clientModel;
		return this;
	}

	public Capabilities getCapabilities() {
		return capabilities;
	}

	public OhaiMessage setCapabilities(Capabilities capabilities) {
		this.capabilities = capabilities;
		return this;
	}
}
