package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;

import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ErrorMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AssociationMessageHandler {
	private final AssociatedClientsManager associatedClientsManager;
	private final MessageSender messageSender;

	public boolean handleAssociateMessage(InetSocketAddress sender, AssociateMessage message) {
		try {
			ClientAssociation association = associatedClientsManager.associate(sender, message.getHostId());
			AssociatedMessage response = new AssociatedMessage()
				.setRtpPanelToMatrixPort(association.getRtpPorts().getPanelToMatrix())
				.setRtpMatrixToPanelPort(association.getRtpPorts().getMatrixToPanel());

			messageSender.sendMessageTo(association.getHostId(), response);
			return true;
		} catch (Exception e) {
			ErrorMessage errorMessage = new ErrorMessage()
				.setMessage(e.getMessage());

			messageSender.sendMessageTo(sender, errorMessage);
			return false;
		}
	}

	public void handleDeAssociateMessage(String hostId, DeAssociateMessage message) {
		DeAssociatedMessage response = new DeAssociatedMessage()
			.setReason(String.format(
				"Received DeAssociateMessage with reason: '%s'",
				message.getReason()));

		messageSender.sendMessageTo(hostId, response);

		associatedClientsManager.deAssociate(hostId);
	}
}
