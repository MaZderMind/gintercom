package de.mazdermind.gintercom.shared.controlserver.messages.registration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

import de.mazdermind.gintercom.shared.controlserver.ClientConfiguration;


public class PanelRegistrationMessage {
	@NotNull
	private String hostId;

	@NotNull
	private Integer protocolVersion;

	@NotNull
	private String clientModel;

	@Valid
	@NotNull
	private Capabilities capabilities;

	public static PanelRegistrationMessage fromClientConfiguration(ClientConfiguration clientConfiguration) {
		return new PanelRegistrationMessage()
			.setHostId(clientConfiguration.getHostId())
			.setClientModel(clientConfiguration.getClientModel())
			.setProtocolVersion(clientConfiguration.getProtocolVersion())
			.setCapabilities(new Capabilities()
				.setButtons(clientConfiguration.getButtons()));
	}

	public String getHostId() {
		return hostId;
	}

	public PanelRegistrationMessage setHostId(String hostId) {
		this.hostId = hostId;
		return this;
	}

	public Integer getProtocolVersion() {
		return protocolVersion;
	}

	public PanelRegistrationMessage setProtocolVersion(Integer protocolVersion) {
		this.protocolVersion = protocolVersion;
		return this;
	}

	public String getClientModel() {
		return clientModel;
	}

	public PanelRegistrationMessage setClientModel(String clientModel) {
		this.clientModel = clientModel;
		return this;
	}

	public Capabilities getCapabilities() {
		return capabilities;
	}

	public PanelRegistrationMessage setCapabilities(Capabilities capabilities) {
		this.capabilities = capabilities;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hostId, protocolVersion, clientModel, capabilities);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PanelRegistrationMessage that = (PanelRegistrationMessage) o;
		return Objects.equal(hostId, that.hostId) &&
			Objects.equal(protocolVersion, that.protocolVersion) &&
			Objects.equal(clientModel, that.clientModel) &&
			Objects.equal(capabilities, that.capabilities);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("hostId", hostId)
			.append("protocolVersion", protocolVersion)
			.append("clientModel", clientModel)
			.append("capabilities", capabilities)
			.toString();
	}
}
