package de.mazdermind.gintercom.shared.controlserver.discovery.impl;

import java.util.Optional;

import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.shared.controlserver.discovery.manualconfig.ManualMatrixAddressConfiguration;

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
