package de.mazdermind.gintercom.mixingcore.portpool;

public class PortSetPool {
	private final PortPool matrixToClientPool;
	private final PortPool clientToMatrixPool;

	public PortSetPool(PortPoolConfig matrixToClient, PortPoolConfig clientToMatrix) {
		matrixToClientPool = new PortPool(matrixToClient);
		clientToMatrixPool = new PortPool(clientToMatrix);
	}

	public synchronized PortSet getNextPortSet() {
		return new PortSet(
				matrixToClientPool.getNextPort(),
				clientToMatrixPool.getNextPort()
		);
	}
}
