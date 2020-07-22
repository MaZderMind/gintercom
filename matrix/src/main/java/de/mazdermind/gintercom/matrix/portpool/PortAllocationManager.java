package de.mazdermind.gintercom.matrix.portpool;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.Config;

@Component
public class PortAllocationManager {

	private final PortPool matrixToClient;
	private final PortPool clientToMatrix;
	private final Map<String, PortSet> allocatedPorts = new HashMap<>();

	public PortAllocationManager(@Autowired Config config) {
		matrixToClient = new PortPool(config.getMatrixConfig().getPorts().getMatrixToClient());
		clientToMatrix = new PortPool(config.getMatrixConfig().getPorts().getClientToMatrix());
	}

	public PortSet allocatePortSet(String clientId) {
		return allocatedPorts.computeIfAbsent(clientId, (key) ->
			new PortSet(
				matrixToClient.getNextPort(),
				clientToMatrix.getNextPort()
			));
	}
}
