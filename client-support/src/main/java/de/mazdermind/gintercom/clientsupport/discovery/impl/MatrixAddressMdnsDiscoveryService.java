package de.mazdermind.gintercom.clientsupport.discovery.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientsupport.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.clientsupport.discovery.MatrixAddressDiscoveryServiceResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(3)
public class MatrixAddressMdnsDiscoveryService implements MatrixAddressDiscoveryServiceImplementation {
	private static final String MDNS_DOMAIN = "matrix.gintercom.local";

	@Override
	public Optional<MatrixAddressDiscoveryServiceResult> tryDiscovery() {
		log.info("Trying to resolve {}", MDNS_DOMAIN);
		try {
			InetAddress address = InetAddress.getByName(MDNS_DOMAIN);
			return Optional.of(
				new MatrixAddressDiscoveryServiceResult(address)
			);
		} catch (UnknownHostException e) {
			log.info("Unknown Host {}", MDNS_DOMAIN);
			return Optional.empty();
		}
	}

	@Override
	public String getDisplayName() {
		return "MDNS (" + MDNS_DOMAIN + ")";
	}
}
