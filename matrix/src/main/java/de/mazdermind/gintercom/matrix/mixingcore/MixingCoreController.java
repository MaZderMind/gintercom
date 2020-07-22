package de.mazdermind.gintercom.matrix.mixingcore;

import javax.annotation.PreDestroy;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.configuration.ButtonDirection;
import de.mazdermind.gintercom.clientapi.configuration.ButtonTargetType;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.MembershipChangeMessage;
import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.mixingcore.Client;
import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MixingCoreController {
	private final MixingCore mixingCore;

	@EventListener
	public void handleClientAssociatedEvent(ClientAssociatedEvent clientAssociatedEvent) {
		ClientAssociation association = clientAssociatedEvent.getAssociation();

		log.debug("Adding Client {}", association.getClientId());
		mixingCore.addClient(
			association.getClientId(),
			association.getSocketAddress().getAddress(),
			association.getRtpPorts().getClientToMatrix(),
			association.getRtpPorts().getMatrixToClient()
		);
	}

	@EventListener
	public void handleClientDeAssociatedEvent(ClientDeAssociatedEvent clientDeAssociatedEvent) {
		ClientAssociation association = clientDeAssociatedEvent.getAssociation();

		log.debug("Removing Client {}", association.getClientId());
		Client client = mixingCore.getClientById(association.getClientId());
		mixingCore.removeClient(client);
	}

	@EventListener
	public void handleMembershipChangeMessage(MembershipChangeMessage.ClientMessage clientMessage) {
		MembershipChangeMessage membershipChangeMessage = clientMessage.getMessage();
		log.info("Received MembershipChangeMessage Message from {}: {}", clientMessage.getClientId(), membershipChangeMessage);

		Client client = mixingCore.getClientById(clientMessage.getClientId());

		MembershipChangeMessage.Change change = membershipChangeMessage.getChange();
		ButtonDirection direction = membershipChangeMessage.getDirection();

		if (membershipChangeMessage.getTargetType() == ButtonTargetType.GROUP) {
			Group group = mixingCore.getGroupById(membershipChangeMessage.getTarget());

			if (change == MembershipChangeMessage.Change.JOIN && direction == ButtonDirection.RX) {
				client.startReceivingFrom(group);
			} else if (change == MembershipChangeMessage.Change.LEAVE && direction == ButtonDirection.RX) {
				client.stopReceivingFrom(group);
			} else if (change == MembershipChangeMessage.Change.JOIN && direction == ButtonDirection.TX) {
				client.startTransmittingTo(group);
			} else if (change == MembershipChangeMessage.Change.LEAVE && direction == ButtonDirection.TX) {
				client.stopTransmittingTo(group);
			}
		} else if (membershipChangeMessage.getTargetType() == ButtonTargetType.PANEL) {
			log.error("Panel-to-Panel-Messages are not yet implemented");
		}
	}

	@PreDestroy
	public void shutdownPipeline() {
		log.info("Shutting MixingCore Down");
		mixingCore.shutdown();
	}
}
