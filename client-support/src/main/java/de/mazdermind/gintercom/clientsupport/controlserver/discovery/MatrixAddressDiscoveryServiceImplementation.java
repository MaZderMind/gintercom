package de.mazdermind.gintercom.clientsupport.controlserver.discovery;

import java.util.Optional;

public interface MatrixAddressDiscoveryServiceImplementation {
	Optional<MatrixAddressDiscoveryServiceResult> tryDiscovery();

	String getDisplayName();
}
