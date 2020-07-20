package de.mazdermind.gintercom.matrix.portpool;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.Config;

@Component
public class PortAllocationManager {

	private final PortPool matrixToPanel;
	private final PortPool panelToMatrix;
	private final Map<String, PortSet> allocatedPorts = new HashMap<>();

	public PortAllocationManager(@Autowired Config config) {
		matrixToPanel = new PortPool(config.getMatrixConfig().getPorts().getMatrixToPanel());
		panelToMatrix = new PortPool(config.getMatrixConfig().getPorts().getPanelToMatrix());
	}

	public PortSet allocatePortSet(String clientId) {
		return allocatedPorts.computeIfAbsent(clientId, (key) ->
			new PortSet(
				matrixToPanel.getNextPort(),
				panelToMatrix.getNextPort()
			));
	}
}
