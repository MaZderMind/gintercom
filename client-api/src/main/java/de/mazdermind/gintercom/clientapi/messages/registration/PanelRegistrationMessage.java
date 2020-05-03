package de.mazdermind.gintercom.clientapi.messages.registration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.mazdermind.gintercom.clientapi.configuration.ClientConfiguration;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
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
}
