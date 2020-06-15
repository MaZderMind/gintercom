package de.mazdermind.gintercom.clientsupport.discovery;

import java.util.Optional;

public interface MatrixAddressDiscoveryServiceImplementation {
	Optional<MatrixAddressDiscoveryServiceResult> tryDiscovery();

	String getDisplayName();
}
