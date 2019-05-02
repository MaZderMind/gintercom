package de.mazdermind.gintercom.shared.controlserver.discovery.impl;

import java.net.InetAddress;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceResult;

// TODO remove -- Test Only
@Component
@Lazy
@Order(99)
public class MatrixAddressLocalhostDiscoveryService implements MatrixAddressDiscoveryServiceImplementation {
	private static Logger log = LoggerFactory.getLogger(MatrixAddressLocalhostDiscoveryService.class);

	@Override
	public Optional<MatrixAddressDiscoveryServiceResult> tryDiscovery() {
		log.info("Trying localhost");
		InetAddress address = InetAddress.getLoopbackAddress();
		return Optional.of(
			new MatrixAddressDiscoveryServiceResult(address)
		);
	}

	@Override
	public String getDisplayName() {
		return "Localhost";
	}
}
