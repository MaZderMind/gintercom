package de.mazdermind.gintercom.matrix.mixingcore;

import javax.annotation.PreDestroy;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Client;
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

		log.debug("Removing Panel {}", association.getClientId());
		Client client = mixingCore.getClientById(association.getClientId());
		mixingCore.removeClient(client);
	}

	@PreDestroy
	public void shutdownPipeline() {
		log.info("Shutting MixingCore Down");
		mixingCore.shutdown();
	}
}
