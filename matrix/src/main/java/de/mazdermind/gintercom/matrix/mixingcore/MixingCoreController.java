package de.mazdermind.gintercom.matrix.mixingcore;

import javax.annotation.PreDestroy;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Panel;
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

		log.debug("Adding Client {}", association.getHostId());
		mixingCore.addPanel(
			association.getHostId(),
			association.getSocketAddress().getAddress(),
			association.getRtpPorts().getPanelToMatrix(),
			association.getRtpPorts().getMatrixToPanel()
		);
	}

	@EventListener
	public void handleClientDeAssociatedEvent(ClientDeAssociatedEvent clientDeAssociatedEvent) {
		ClientAssociation association = clientDeAssociatedEvent.getAssociation();

		log.debug("Removing Panel {}", association.getHostId());
		Panel panel = mixingCore.getPanelByName(association.getHostId());
		mixingCore.removePanel(panel);
	}

	@PreDestroy
	public void shutdownPipeline() {
		log.info("Shutting MixingCore Down");
		mixingCore.shutdown();
	}
}
