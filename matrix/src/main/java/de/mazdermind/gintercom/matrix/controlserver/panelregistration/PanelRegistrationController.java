package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import static de.mazdermind.gintercom.matrix.configuration.framework.IpAddressHandshakeInterceptor.IP_ADDRESS_ATTRIBUTE;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import de.mazdermind.gintercom.matrix.configuration.ButtonSetResolver;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.portpool.PortSet;
import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.shared.controlserver.messages.registration.PanelRegistrationMessage;
import de.mazdermind.gintercom.shared.controlserver.provisioning.ProvisioningInformation;

@Controller
public class PanelRegistrationController {
	private static final Logger log = LoggerFactory.getLogger(PanelRegistrationController.class);

	private final Config config;
	private final PortAllocationManager portAllocationManager;
	private final PanelRegistrationAwareMulticaster panelRegistrationAwareMulticaster;
	private final ButtonSetResolver buttonSetResolver;

	private final Map<String, String> registeredPanels = new HashMap<>();

	public PanelRegistrationController(
		@Autowired Config config,
		@Autowired PortAllocationManager portAllocationManager,
		@Autowired PanelRegistrationAwareMulticaster panelRegistrationAwareMulticaster,
		@Autowired ButtonSetResolver buttonSetResolver
	) {
		this.portAllocationManager = portAllocationManager;
		this.config = config;
		this.panelRegistrationAwareMulticaster = panelRegistrationAwareMulticaster;
		this.buttonSetResolver = buttonSetResolver;
	}

	@SuppressWarnings("unused")
	@MessageMapping("/registration")
	@SendToUser("/provision")
	public ProvisionMessage handleRegistrationRequest(
		SimpMessageHeaderAccessor headerAccessor,
		PanelRegistrationMessage message
	) {
		String sessionId = headerAccessor.getSessionId();
		InetAddress hostAddress = (InetAddress) headerAccessor.getSessionAttributes().get(IP_ADDRESS_ATTRIBUTE);
		String hostId = message.getHostId();
		log.info("Host-Id {}: Received PanelRegistration-Message", hostId);

		Optional<String> maybePanelId = config.findPanelIdForHostId(hostId);
		if (!maybePanelId.isPresent()) {
			log.info("Host-Id {}: Currently unknown in Config and needs to be Provisioned in WebUI.", hostId);
			return null;
		}

		String panelId = maybePanelId.get();
		PanelConfig panelConfig = config.getPanels().get(panelId);
		PortSet portSet = portAllocationManager.allocatePortSet(hostId);

		log.info("Host-Id {}: is known in Config as Panel {} ({}). Allocated Ports {} for it. Localized it at {}. Mapped Stomp-Session-ID {} to it.",
			hostId, panelId, panelConfig.getDisplay(), portSet, hostAddress.getHostAddress(), sessionId);

		registeredPanels.put(sessionId, panelId);

		panelRegistrationAwareMulticaster.dispatchPanelRegistration(new PanelRegistrationEvent(
			panelId, panelConfig, portSet, hostAddress
		));

		log.info("Responding with ProvisionMessage");
		return new ProvisionMessage()
			.setProvisioningInformation(new ProvisioningInformation()
				.setDisplay(panelConfig.getDisplay())
				.setMatrixToPanelPort(portSet.getMatrixToPanel())
				.setPanelToPanelPort(portSet.getPanelToMatrix())
				.setButtons(buttonSetResolver.resolveButtons(panelConfig)));
	}

	@EventListener
	public void handlePanelDisconnect(SessionDisconnectEvent sessionDisconnectEvent) {
		String sessionId = sessionDisconnectEvent.getSessionId();
		String panelId = registeredPanels.remove(sessionId);
		if (panelId != null) {
			log.info("Found Panel-ID {} for Stomp-Session-ID {}", panelId, sessionId);

			PanelConfig panelConfig = config.getPanels().get(panelId);
			log.info("Session of Panel {} ({}) closed", panelId, panelConfig.getDisplay());

			panelRegistrationAwareMulticaster.dispatchPanelDeRegistration(new PanelDeRegistrationEvent(
				panelId
			));
		}
	}
}
