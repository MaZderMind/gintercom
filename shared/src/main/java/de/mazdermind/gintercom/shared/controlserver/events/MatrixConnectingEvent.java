package de.mazdermind.gintercom.shared.controlserver.events;

import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceResult;

public class MatrixConnectingEvent implements MatrixConnectionLifecycleEvent {
	private final MatrixAddressDiscoveryServiceResult discoveredMatrix;

	public MatrixConnectingEvent(MatrixAddressDiscoveryServiceResult discoveredMatrix) {
		this.discoveredMatrix = discoveredMatrix;
	}

	@Override
	public String getMessage() {
		return String.format("Trying to Connect to Matrix at %s (%s:%d)",
			discoveredMatrix.getAddress().getHostName(),
			discoveredMatrix.getAddress().getHostAddress(),
			discoveredMatrix.getPort());
	}
}
