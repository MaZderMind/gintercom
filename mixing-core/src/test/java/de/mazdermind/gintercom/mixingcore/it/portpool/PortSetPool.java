package de.mazdermind.gintercom.mixingcore.it.portpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortSetPool {
	private static final Logger log = LoggerFactory.getLogger(PortSetPool.class);
	private final PortPool matrixToPanelPool;
	private final PortPool panelToMatrixPool;

	public PortSetPool(PortPoolConfig matrixToPanel, PortPoolConfig panelToMatrix) {
		matrixToPanelPool = new PortPool(matrixToPanel);
		panelToMatrixPool = new PortPool(panelToMatrix);
	}

	public synchronized PortSet getNextPortSet() {
		return new PortSet(
				matrixToPanelPool.getNextPort(),
				panelToMatrixPool.getNextPort()
		);
	}
}
