package de.mazdermind.gintercom.matrix.controlserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import de.mazdermind.gintercom.shared.controlserver.model.ohai.OhaiMessage;
import de.mazdermind.gintercom.shared.controlserver.model.provision.ProvisionMessage;

@Controller
public class PanelRegistrationController {
	private static Logger log = LoggerFactory.getLogger(PanelRegistrationController.class);

	@SuppressWarnings("unused")
	@MessageMapping("/ohai")
	@SendTo("/provision")
	public ProvisionMessage handleRegistrationRequest(OhaiMessage message) {
		log.info("Received Ohai-Message from {}, responding with Provision-Message", message.getClientId());

		return new ProvisionMessage()
			.setDisplay("Foobar 42!");
	}
}
