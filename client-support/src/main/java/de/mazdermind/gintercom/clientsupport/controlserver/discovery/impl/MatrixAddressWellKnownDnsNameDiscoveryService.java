package de.mazdermind.gintercom.clientsupport.controlserver.discovery.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
public class MatrixAddressWellKnownDnsNameDiscoveryService implements MatrixAddressDiscoveryServiceImplementation {
	private static final String WELL_KNOWN_DOMAIN = "matrix.gintercom.lan";

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
