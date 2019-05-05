package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import de.mazdermind.gintercom.matrix.configuration.framework.IpAddressHandshakeInterceptor;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.portpool.PortSet;
import de.mazdermind.gintercom.shared.controlserver.messages.ohai.OhaiMessage;

@Controller
public class PanelRegistrationController {
	private static Logger log = LoggerFactory.getLogger(PanelRegistrationController.class);

	private final Config config;
	private final PortAllocationManager portAllocationManager;
	private final PanelRegistrationAwareMulticaster panelRegistrationAwareMulticaster;

	private final Map<String, String> registeredPanels = new HashMap<>();

	public PanelRegistrationController(
		@Autowired Config config,
		@Autowired PortAllocationManager portAllocationManager,
		@Autowired PanelRegistrationAwareMulticaster panelRegistrationAwareMulticaster
	) {
		this.portAllocationManager = portAllocationManager;
		this.config = config;
		this.panelRegistrationAwareMulticaster = panelRegistrationAwareMulticaster;
	}

	@SuppressWarnings("unused")
	@MessageMapping("/ohai")
	public void handleRegistrationRequest(
		OhaiMessage message,
		StompSession stompSession,
		@Header(name = IpAddressHandshakeInterceptor.IP_ADDRESS_ATTRIBUTR) InetAddress hostAdress
	) {
		String hostId = message.getClientId();
		log.info("Received Ohai-Message from Host-Id {} at {}", hostId, hostAdress);

		Optional<String> maybePanelId = config.findPanelIdForHostId(hostId);
		if (!maybePanelId.isPresent()) {
			log.info("Host-Id {} is currently unknown in Config and needs to be Provisioned in WebUI.", hostId);
			return;
		}

		String panelId = maybePanelId.get();
		PanelConfig panelConfig = config.getPanels().get(panelId);
		PortSet portSet = portAllocationManager.allocatePortSet(hostId);

		log.info("Host-Id {} is known in Config as Panel {} ({}). Allocated Ports {} for it.",
			hostId, panelId, panelConfig.getDisplay(), portSet);

		registeredPanels.put(stompSession.getSessionId(), panelId);
		panelRegistrationAwareMulticaster.dispatchPanelRegistration(new PanelRegistrationEvent(
			panelId, panelConfig, portSet, hostAdress
		));
	}

	@EventListener
	public void handlePanelDisconnect(SessionDisconnectEvent sessionDisconnectEvent) {
		String sessionId = sessionDisconnectEvent.getSessionId();
		String panelId = registeredPanels.remove(sessionId);
		if (panelId != null) {
			PanelConfig panelConfig = config.getPanels().get(panelId);
			log.info("Session of Panel {} ({}) closed", panelId, panelConfig.getDisplay());

			panelRegistrationAwareMulticaster.dispatchPanelDeRegistration(new PanelDeRegistrationEvent(
				panelId
			));
		}
	}
}
