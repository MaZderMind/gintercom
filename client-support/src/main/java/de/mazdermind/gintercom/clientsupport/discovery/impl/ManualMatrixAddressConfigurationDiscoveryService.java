package de.mazdermind.gintercom.clientsupport.discovery.impl;

import java.net.InetSocketAddress;
import java.util.Optional;

import de.mazdermind.gintercom.clientsupport.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.clientsupport.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.clientsupport.discovery.manualconfig.ManualMatrixAddressConfiguration;

public class ManualMatrixAddressConfigurationDiscoveryService implements MatrixAddressDiscoveryServiceImplementation {
	private final ManualMatrixAddressConfiguration manualConfiguration;

	public ManualMatrixAddressConfigurationDiscoveryService(ManualMatrixAddressConfiguration manualConfiguration) {
		this.manualConfiguration = manualConfiguration;
	}

	@Override
	public Optional<MatrixAddressDiscoveryServiceResult> tryDiscovery() {
		return Optional.of(
			new MatrixAddressDiscoveryServiceResult(
				new InetSocketAddress(manualConfiguration.getAddress(),
					manualConfiguration.getPort())));
	}

	@Override
	public String getDisplayName() {
		return "Manual Configuration";
	}
}
