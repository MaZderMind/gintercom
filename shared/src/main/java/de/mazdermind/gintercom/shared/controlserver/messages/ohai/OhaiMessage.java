package de.mazdermind.gintercom.shared.controlserver.messages.ohai;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.mazdermind.gintercom.shared.controlserver.GintercomClientConfiguration;


public class OhaiMessage {
	@NotNull
	private String clientId;

	@NotNull
	private Integer protocolVersion;

	@NotNull
	private String clientModel;

	@Valid
	private Capabilities capabilities;

	public static OhaiMessage fromClientConfiguration(GintercomClientConfiguration clientConfiguration) {
		return new OhaiMessage()
			.setClientId(clientConfiguration.getClientId())
			.setClientModel(clientConfiguration.getClientModel())
			.setProtocolVersion(clientConfiguration.getProtocolVersion())
			.setCapabilities(new Capabilities()
				.setButtons(clientConfiguration.getButtons()));
	}

	public String getClientId() {
		return clientId;
	}

	public OhaiMessage setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public Integer getProtocolVersion() {
		return protocolVersion;
	}

	public OhaiMessage setProtocolVersion(Integer protocolVersion) {
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
