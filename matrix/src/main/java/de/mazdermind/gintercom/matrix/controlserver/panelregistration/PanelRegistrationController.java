package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.mazdermind.gintercom.matrix.frameworkconfig.IpAddressHandshakeInterceptor.IP_ADDRESS_ATTRIBUTE;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import de.mazdermind.gintercom.clientapi.messages.provision.AlreadyRegisteredMessage;
import de.mazdermind.gintercom.clientapi.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import de.mazdermind.gintercom.clientapi.messages.registration.PanelRegistrationMessage;
import de.mazdermind.gintercom.matrix.configuration.ButtonSetResolver;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.portpool.PortSet;
import de.mazdermind.gintercom.matrix.webui.UiUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PanelRegistrationController {
	private final Config config;
	private final PortAllocationManager portAllocationManager;
	private final ApplicationEventPublisher eventPublisher;
	private final ButtonSetResolver buttonSetResolver;
	private final PanelConnectionManager panelConnectionManager;
	private final SimpReponder simpReponder;

	@SuppressWarnings("unused")
	@MessageMapping("/registration")
	public void handleRegistrationRequest(
		SimpMessageHeaderAccessor headerAccessor,
		PanelRegistrationMessage message
	) {
		String sessionId = checkNotNull(headerAccessor.getSessionId());
		Map<String, Object> sessionAttributes = checkNotNull(headerAccessor.getSessionAttributes());
		InetAddress hostAddress = (InetAddress) sessionAttributes.get(IP_ADDRESS_ATTRIBUTE);
		String hostId = message.getHostId();
		log.info("Host-Id {}: Received PanelRegistration-Message on Session {}", hostId, sessionId);

		Optional<PanelConnectionInformation> maybePanelAlreadyRegistered = panelConnectionManager.getConnectionInformationForHostId(hostId);
		if (maybePanelAlreadyRegistered.isPresent()) {
			PanelConnectionInformation panelConnectionInformation = maybePanelAlreadyRegistered.get();
			log.info("Host-Id {} is already registered at {} since {}, rejecting second registration",
				hostId, panelConnectionInformation.getRemoteIp(), panelConnectionInformation.getConnectionTime());

			AlreadyRegisteredMessage alreadyRegisteredMessage = new AlreadyRegisteredMessage()
				.setRemoteIp(panelConnectionInformation.getRemoteIp())
				.setConnectionTime(panelConnectionInformation.getConnectionTime());

			simpReponder.convertAndRespondToUser(sessionId,
				"/provision/already-registered", alreadyRegisteredMessage);

			return;
		}

		Optional<String> maybePanelId = config.findPanelIdForHostId(hostId);
		panelConnectionManager.registerPanelConnection(sessionId, new PanelConnectionInformation()
			.setConnectionTime(LocalDateTime.now())
			.setHostId(hostId)
			.setPanelId(maybePanelId)
			.setRemoteIp(hostAddress)
			.setSessionId(sessionId));

		if (!maybePanelId.isPresent()) {
			log.info("Host-Id {}: Currently unknown in Config and needs to be Provisioned in WebUI.", hostId);
			eventPublisher.publishEvent(new UiUpdateEvent());

			return;
		}

		String panelId = maybePanelId.get();
		PanelConfig panelConfig = config.getPanels().get(panelId);
		PortSet portSet = portAllocationManager.allocatePortSet(hostId);

		log.info("Host-Id {}: is known in Config as Panel {} ({}). Allocated Ports {} for it. Localized it at {}. Mapped Stomp-Session-ID {} to it.",
			hostId, panelId, panelConfig.getDisplay(), portSet, hostAddress.getHostAddress(), sessionId);

		eventPublisher.publishEvent(new PanelRegistrationEvent(
			panelId, panelConfig, portSet, hostAddress
		));
		eventPublisher.publishEvent(new UiUpdateEvent());

		log.info("Responding with ProvisionMessage");
		ProvisionMessage provisionMessage = new ProvisionMessage()
			.setProvisioningInformation(new ProvisioningInformation()
				.setDisplay(panelConfig.getDisplay())
				.setMatrixToPanelPort(portSet.getMatrixToPanel())
				.setPanelToMatrixPort(portSet.getPanelToMatrix())
				.setButtons(buttonSetResolver.resolveButtons(panelConfig)));

		simpReponder.convertAndRespondToUser(sessionId, "/provision", provisionMessage);
	}

	@EventListener
	public void handlePanelDisconnect(SessionDisconnectEvent sessionDisconnectEvent) {
		String sessionId = sessionDisconnectEvent.getSessionId();
		Optional<PanelConnectionInformation> panelConnectionInformation = panelConnectionManager.deregisterPanelConnection(sessionId);
		if (!panelConnectionInformation.isPresent()) {
			log.info("Received Disconnect-Event for Stomp-Session-ID {} which is not connected", sessionId);
			return;
		}

		eventPublisher.publishEvent(new UiUpdateEvent());

		String hostId = panelConnectionInformation.get().getHostId();
		Optional<String> maybePanelId = config.findPanelIdForHostId(hostId);

		if (maybePanelId.isPresent()) {
			String panelId = maybePanelId.get();
			log.info("Found Panel-ID {} for Stomp-Session-ID {}", panelId, sessionId);

			PanelConfig panelConfig = config.getPanels().get(panelId);
			log.info("Session of Panel {} ({}) closed", panelId, panelConfig.getDisplay());

			eventPublisher.publishEvent(new PanelDeRegistrationEvent(
				panelId
			));
		}
	}
}
