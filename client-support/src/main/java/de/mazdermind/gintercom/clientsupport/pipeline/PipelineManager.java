package de.mazdermind.gintercom.clientsupport.pipeline;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.clientsupport.events.AssociatedEvent;
import de.mazdermind.gintercom.clientsupport.events.DeAssociatedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PipelineManager {
	private final ClientPipeline clientPipeline;

	public PipelineManager(
		@Autowired ClientPipeline clientPipeline
	) {
		this.clientPipeline = clientPipeline;
		log.info("Using ClientPipeline-Implementation {}", clientPipeline.getClass().getName());
	}

	@EventListener
	public void handleAssociatedEvent(AssociatedEvent associatedEvent) {
		clientPipeline.configurePipeline(associatedEvent.getMatrixAddress().getAddress(),
			associatedEvent.getRtpMatrixToPanelPort(),
			associatedEvent.getRtpPanelToMatrixPort());

		clientPipeline.startPipeline();
	}

	@EventListener(DeAssociatedEvent.class)
	public void handleDeAssociatedEvent() {
		clientPipeline.destroyPipeline();
	}

	@PreDestroy
	public void destroyPipeline() {
		clientPipeline.destroyPipeline();
	}
}
