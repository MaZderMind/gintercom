package de.mazdermind.gintercom.mixingcore.portpool;

public class PortSetPool {
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
