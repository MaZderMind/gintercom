package de.mazdermind.gintercom.clientsupport.controlserver.discovery.impl;

import java.util.Optional;

import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.manualconfig.ManualMatrixAddressConfiguration;

public class ManualMatrixAddressConfigurationDiscoveryService implements MatrixAddressDiscoveryServiceImplementation {
	private final ManualMatrixAddressConfiguration manualConfiguration;

	public ManualMatrixAddressConfigurationDiscoveryService(ManualMatrixAddressConfiguration manualConfiguration) {
		this.manualConfiguration = manualConfiguration;
	}

	@Override
	public Optional<MatrixAddressDiscoveryServiceResult> tryDiscovery() {
		return Optional.of(
			new MatrixAddressDiscoveryServiceResult(
				manualConfiguration.getAddress(),
				manualConfiguration.getPort()));
	}

	@Override
	public String getDisplayName() {
		return "Manual Configuration";
	}
}
