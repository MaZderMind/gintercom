package de.mazdermind.gintercom.shared.controlserver.discovery;

import static de.mazdermind.gintercom.shared.utils.ObjectListClassNameUtil.classNamesList;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class MatrixAddressDiscoveryService {
	private static Logger log = LoggerFactory.getLogger(MatrixAddressDiscoveryService.class);

	private final Iterator<MatrixAddressDiscoveryServiceImplementation> implementationsIterator;

	public MatrixAddressDiscoveryService(
		@Autowired List<MatrixAddressDiscoveryServiceImplementation> implementations
	) {
		assert implementations.size() > 0;
		this.implementationsIterator = IteratorUtils.loopingIterator(implementations);
		log.info("Found {} Discovery Implementations: {}", implementations.size(), classNamesList(implementations));
	}

	public MatrixAddressDiscoveryServiceImplementation getNextImplementation() {
		return implementationsIterator.next();
	}
}
