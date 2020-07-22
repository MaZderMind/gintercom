package de.mazdermind.gintercom.matrix.controlserver;

import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeProvisionMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ProvisionMessage;
import de.mazdermind.gintercom.matrix.configuration.ButtonSetResolver;
import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.PanelGroupsChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProvisioningManager {
	private final Config config;
	private final ApplicationEventPublisher eventPublisher;
	private final MessageSender messageSender;
	private final ButtonSetResolver buttonSetResolver;

	@EventListener
	@Order(Ordered.LOWEST_PRECEDENCE)
	public void handleConfiguredClientAssociation(ClientAssociatedEvent associatedEvent) {
		String clientId = associatedEvent.getAssociation().getClientId();
		Optional<String> maybePanelId = config.findPanelIdForClientId(clientId);

		if (maybePanelId.isPresent()) {
			String panelId = maybePanelId.get();
			PanelConfig panelConfig = config.getPanels().get(panelId);

			log.info("Sending ProvisionMessage for Panel {} to Client-Id {}",
				panelConfig.getDisplay(), clientId);

			Map<String, ButtonConfig> resolvedButtons = buttonSetResolver.resolveButtons(panelConfig);
			messageSender.sendMessageTo(clientId, new ProvisionMessage()
				.setDisplay(panelConfig.getDisplay())
				.setButtons(resolvedButtons)
				.setRxGroups(panelConfig.getRxGroups())
				.setTxGroups(panelConfig.getTxGroups()));

			// Setup initial RX/TX Group Configuration
			eventPublisher.publishEvent(new PanelGroupsChangedEvent()
				.setAssociation(associatedEvent.getAssociation())
				.setRxGroups(panelConfig.getRxGroups())
				.setTxGroups(panelConfig.getTxGroups()));
		}
	}

	@EventListener
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public void handleConfiguredClientDeAssociation(ClientDeAssociatedEvent deAssociatedEvent) {
		String clientId = deAssociatedEvent.getAssociation().getClientId();
		Optional<String> maybePanelId = config.findPanelIdForClientId(clientId);

		if (maybePanelId.isPresent()) {
			String panelId = maybePanelId.get();
			PanelConfig panelConfig = config.getPanels().get(panelId);

			log.info("Sending DeProvisionMessage for Panel {} to Client-Id {}",
				panelConfig.getDisplay(), clientId);

			messageSender.sendMessageTo(clientId, new DeProvisionMessage());
		}
	}
}
