package de.mazdermind.gintercom.clientsupport.controlserver.discovery.impl;

import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceImplementation;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(2)
public class MatrixAddressDnsSdDiscoveryService implements MatrixAddressDiscoveryServiceImplementation {
	private static final String DNSSD_DOMAIN = "_gintercom._tcp";

	@Override
	public Optional<MatrixAddressDiscoveryServiceResult> tryDiscovery() {
		log.warn("Not yet Implemented");
		return Optional.empty();
	}

	@Override
	public String getDisplayName() {
		return "DNS-SD (" + DNSSD_DOMAIN + ")";
	}
}
