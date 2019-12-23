package de.mazdermind.gintercom.shared.controlserver.discovery.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.shared.controlserver.discovery.MatrixAddressDiscoveryServiceResult;

@Component
@Lazy
@Order(1)
public class MatrixAddressWellKnownDnsNameDiscoveryService implements MatrixAddressDiscoveryServiceImplementation {
	private static final String WELL_KNOWN_DOMAIN = "matrix.gintercom.lan";
	private static final Logger log = LoggerFactory.getLogger(MatrixAddressWellKnownDnsNameDiscoveryService.class);

	@Override
	public Optional<MatrixAddressDiscoveryServiceResult> tryDiscovery() {
		try {
			log.info("Trying to resolve {}", WELL_KNOWN_DOMAIN);
			InetAddress address = InetAddress.getByName(WELL_KNOWN_DOMAIN);
			return Optional.of(
				new MatrixAddressDiscoveryServiceResult(address)
			);
		} catch (UnknownHostException e) {
			log.info("Unknown Host {}", WELL_KNOWN_DOMAIN);
			return Optional.empty();
		}
	}

	@Override
	public String getDisplayName() {
		return "Well-Known Domain (" + WELL_KNOWN_DOMAIN + ")";
	}
}
