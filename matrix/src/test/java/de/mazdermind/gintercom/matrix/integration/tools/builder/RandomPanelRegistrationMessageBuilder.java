package de.mazdermind.gintercom.matrix.integration.tools.builder;

import static com.oblac.nomen.Nomen.randomName;
import static de.mazdermind.gintercom.shared.hostid.RandomHostIdGenerator.generateRandomHostId;
import static java.util.Collections.emptyList;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.Capabilities;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;

public class RandomPanelRegistrationMessageBuilder {
	private static final int DEFAULT_PROTOCOL_VERSION = 1;

	public static PanelRegistrationMessage randomPanelRegistrationMessage() {
		return new PanelRegistrationMessage()
			.setClientModel("model_" + randomName())
			.setProtocolVersion(DEFAULT_PROTOCOL_VERSION)
			.setHostId(generateRandomHostId())
			.setCapabilities(new Capabilities()
				.setButtons(emptyList()));
	}

	public static PanelRegistrationMessage randomPanelRegistrationMessageForPanelConfig(PanelConfig panelConfig) {
		return randomPanelRegistrationMessage()
			.setHostId(panelConfig.getHostId());
	}
}