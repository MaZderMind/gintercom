package de.mazdermind.gintercom.clientsupport.pipeline;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import de.mazdermind.gintercom.clientsupport.controlserver.ConnectionLifecycleManager;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PipelineManager {
	private final ClientPipeline clientPipeline;
	private final ConnectionLifecycleManager connectionLifecycleManager;

	public PipelineManager(
		@Autowired ClientPipeline clientPipeline,
		@Autowired ConnectionLifecycleManager connectionLifecycleManager
	) {
		this.clientPipeline = clientPipeline;
		this.connectionLifecycleManager = connectionLifecycleManager;
		log.info("Using ClientPipeline-Implementation {}", clientPipeline.getClass().getName());
	}

	@EventListener
	public void handleProvisioningInformation(ProvisioningInformation provisioningInformation) {
		Optional<MatrixAddressDiscoveryServiceResult> discoveredMatrix = connectionLifecycleManager.getDiscoveredMatrix();
		if (!discoveredMatrix.isPresent()) {
			log.error("Not Connected to Matrix anymore");
			return;
		}

		clientPipeline.configurePipeline(discoveredMatrix.get(), provisioningInformation);
		clientPipeline.startPipeline();
	}
}
