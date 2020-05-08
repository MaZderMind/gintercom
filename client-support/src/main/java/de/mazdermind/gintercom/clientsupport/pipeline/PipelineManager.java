package de.mazdermind.gintercom.clientsupport.pipeline;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.clientsupport.controlserver.events.provision.DeProvisionEvent;
import de.mazdermind.gintercom.clientsupport.controlserver.events.provision.ProvisionEvent;
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
	public void provisionPipeline(ProvisionEvent provisionEvent) {
		clientPipeline.configurePipeline(provisionEvent.getMatrixAddress(), provisionEvent.getProvisioningInformation());
		clientPipeline.startPipeline();
	}

	@EventListener
	public void deProvisionPipeline(DeProvisionEvent deProvisionEvent) {
		clientPipeline.destroyPipeline();
	}

	@PreDestroy
	public void destroyPipeline() {
		clientPipeline.destroyPipeline();
	}
}
