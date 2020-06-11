package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;

import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ErrorMessage;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AssociationMessageHandler {
	private final AssociatedClientsManager associatedClientsManager;
	private final MessageSender messageSender;

	public boolean handleAssociateMessage(InetSocketAddress sender, AssociateMessage message) {
		try {
			associatedClientsManager.associate(sender, message.getHostId());

			// The Messages are sent in an @EventListener(ClientAssociatedEvent) instead of directly sending them from here to ensure
			// correct ordering with other EventListeners (for example the ProvisioningManager) that will send their Messages during the
			// associatedClientsManager.associate call

			return true;
		} catch (Exception e) {
			ErrorMessage errorMessage = new ErrorMessage()
				.setMessage(e.getMessage());

			messageSender.sendMessageTo(sender, errorMessage);
			return false;
		}
	}

	public void handleDeAssociateMessage(String hostId, DeAssociateMessage message) {
		associatedClientsManager.deAssociate(hostId, message.getReason());
	}

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

		String reason = String.format(
			"Received DeAssociateMessage with reason: '%s'",
			deAssociatedEvent.getReason());

		DeAssociatedMessage response = new DeAssociatedMessage()
			.setReason(reason);

		messageSender.sendMessageTo(association.getHostId(), response);
	}
}
