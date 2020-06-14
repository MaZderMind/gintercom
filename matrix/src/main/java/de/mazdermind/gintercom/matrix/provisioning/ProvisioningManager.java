package de.mazdermind.gintercom.matrix.provisioning;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeProvisionMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ProvisionMessage;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.MessageSender;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.PanelAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.PanelDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.portpool.PortSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProvisioningManager {
	private final Config config;
	private final PortAllocationManager portAllocationManager;
	private final ApplicationEventPublisher eventPublisher;
	private final MessageSender messageSender;

	@EventListener
	@Order(Ordered.LOWEST_PRECEDENCE)
	public void handleConfiguredClientAssociation(ClientAssociatedEvent associatedEvent) {
		String hostId = associatedEvent.getAssociation().getHostId();
		Optional<String> maybePanelId = config.findPanelIdForHostId(hostId);

		if (maybePanelId.isPresent()) {
			String panelId = maybePanelId.get();
			PanelConfig panelConfig = config.getPanels().get(panelId);
			PortSet portSet = portAllocationManager.allocatePortSet(panelId);

			log.info("Sending ProvisionMessage for Panel {} to Host-ID {}",
				panelConfig.getDisplay(), hostId);

			messageSender.sendMessageTo(hostId, new ProvisionMessage()
				.setDisplay(panelConfig.getDisplay())
				.setButtons(panelConfig.getButtons()));

			eventPublisher.publishEvent(new PanelAssociatedEvent()
				.setAssociation(associatedEvent.getAssociation())
				.setPortSet(portSet)
				.setPanelId(panelId)
				.setPanelConfig(panelConfig));
		}
	}

	@EventListener
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public void handleConfiguredClientDeAssociation(ClientDeAssociatedEvent deAssociatedEvent) {
		String hostId = deAssociatedEvent.getAssociation().getHostId();
		Optional<String> maybePanelId = config.findPanelIdForHostId(hostId);

		if (maybePanelId.isPresent()) {
			String panelId = maybePanelId.get();
			PanelConfig panelConfig = config.getPanels().get(panelId);

			log.info("Sending DeProvisionMessage for Panel {} to Host-ID {}",
				panelConfig.getDisplay(), hostId);

			messageSender.sendMessageTo(hostId, new DeProvisionMessage());

			eventPublisher.publishEvent(new PanelDeAssociatedEvent()
				.setAssociation(deAssociatedEvent.getAssociation())
				.setPanelId(panelId));
		}
	}
}
