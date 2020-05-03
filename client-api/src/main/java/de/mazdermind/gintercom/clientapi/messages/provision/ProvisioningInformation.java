package de.mazdermind.gintercom.clientapi.messages.provision;

import java.util.Map;

import javax.validation.constraints.NotNull;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProvisioningInformation {
	@NotNull
	private String display;

	@NotNull
	private Integer matrixToPanelPort;

	@NotNull
	private Integer panelToMatrixPort;

	@NotNull
	private Map<String, ButtonConfig> buttons;
}
