package de.mazdermind.gintercom.clientsupport.pipeline;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import de.mazdermind.gintercom.clientsupport.controlserver.ConnectionLifecycleManager;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.clientsupport.controlserver.provisioning.ProvisioningInformationAware;

@Service
public class PipelineManager implements ProvisioningInformationAware {
	private static final Logger log = LoggerFactory.getLogger(PipelineManager.class);

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

	@Override
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
