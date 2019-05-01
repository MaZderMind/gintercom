package de.mazdermind.gintercom.shared.controlserver.discovery;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.events.MatrixAddressDiscoveryEvent;

@Component
@Lazy
public class MatrixAddressDiscoveryService {
	private static Logger log = LoggerFactory.getLogger(MatrixAddressDiscoveryService.class);

	private final Iterator<MatrixAddressDiscoveryServiceImplementation> implementationsIterator;
	private final ApplicationEventPublisher eventPublisher;

	public MatrixAddressDiscoveryService(
		@Autowired List<MatrixAddressDiscoveryServiceImplementation> implementations,
		@Autowired ApplicationEventPublisher eventPublisher
	) {
		this.eventPublisher = eventPublisher;
		assert implementations.size() > 0;
		this.implementationsIterator = IteratorUtils.loopingIterator(implementations);
	}

	public Optional<MatrixAddressDiscoveryServiceResult> tryNext() {
		MatrixAddressDiscoveryServiceImplementation discoveryMethod = implementationsIterator.next();
		log.info("Trying {}", discoveryMethod.getClass().getSimpleName());
		eventPublisher.publishEvent(new MatrixAddressDiscoveryEvent(
			discoveryMethod.getClass().getSimpleName(),
			discoveryMethod.getDisplayName()
		));

		return discoveryMethod.tryDiscovery();
	}
}
