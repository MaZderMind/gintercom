package de.mazdermind.gintercom.shared.controlserver.discovery;

import java.util.Optional;

public interface MatrixAddressDiscoveryServiceImplementation {
	Optional<MatrixAddressDiscoveryServiceResult> tryDiscovery();

	String getDisplayName();
}
