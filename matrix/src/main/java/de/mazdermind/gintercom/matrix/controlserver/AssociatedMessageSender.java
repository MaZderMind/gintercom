package de.mazdermind.gintercom.matrix.controlserver;

import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AssociatedMessageSender {
	private final MessageSender messageSender;

	@EventListener
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public void handleClientAssociatedEvent(ClientAssociatedEvent associatedEvent) {
		ClientAssociation association = associatedEvent.getAssociation();

		AssociatedMessage response = new AssociatedMessage()
			.setRtpPanelToMatrixPort(association.getRtpPorts().getPanelToMatrix())
			.setRtpMatrixToPanelPort(association.getRtpPorts().getMatrixToPanel());

		messageSender.sendMessageTo(association.getHostId(), response);
	}

	@EventListener
	@Order(Ordered.LOWEST_PRECEDENCE)
	public void handleClientDeAssociatedEvent(ClientDeAssociatedEvent deAssociatedEvent) {
		ClientAssociation association = deAssociatedEvent.getAssociation();

		DeAssociatedMessage response = new DeAssociatedMessage()
			.setReason(deAssociatedEvent.getReason());

		messageSender.sendMessageTo(association.getHostId(), response);
	}
}
