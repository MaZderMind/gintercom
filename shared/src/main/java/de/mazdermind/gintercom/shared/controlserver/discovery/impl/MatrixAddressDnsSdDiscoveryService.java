package de.mazdermind.gintercom.shared.controlserver.discovery.impl;

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
@Order(2)
public class MatrixAddressDnsSdDiscoveryService implements MatrixAddressDiscoveryServiceImplementation {
	private static final String DNSSD_DOMAIN = "_gintercom._tcp";
	private static Logger log = LoggerFactory.getLogger(MatrixAddressDnsSdDiscoveryService.class);

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
