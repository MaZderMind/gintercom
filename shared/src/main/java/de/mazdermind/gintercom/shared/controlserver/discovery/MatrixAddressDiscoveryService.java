package de.mazdermind.gintercom.shared.controlserver.discovery;

import static de.mazdermind.gintercom.shared.utils.ObjectListClassNameUtil.classNamesList;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.shared.controlserver.discovery.impl.ManualMatrixAddressConfigurationDiscoveryService;
import de.mazdermind.gintercom.shared.controlserver.discovery.manualconfig.ManualMatrixAddressConfiguration;

@Component
@Lazy
public class MatrixAddressDiscoveryService {
	private static Logger log = LoggerFactory.getLogger(MatrixAddressDiscoveryService.class);

	private final Iterator<MatrixAddressDiscoveryServiceImplementation> implementationsIterator;

	public MatrixAddressDiscoveryService(
		@Autowired List<MatrixAddressDiscoveryServiceImplementation> implementations,
		@SuppressWarnings("OptionalUsedAsFieldOrParameterType") @Autowired(required = false)
			Optional<ManualMatrixAddressConfiguration> manualMatrixAddressConfiguration
	) {
		if (manualMatrixAddressConfiguration.isPresent()) {
			log.info("Manual Configuration {} available, skipping Discovery",
				manualMatrixAddressConfiguration.get());

			this.implementationsIterator = IteratorUtils.loopingIterator(ImmutableList.of(
				new ManualMatrixAddressConfigurationDiscoveryService(manualMatrixAddressConfiguration.get())
			));
		} else {
			assert implementations.size() > 0;
			log.info("Found {} Discovery Implementations: {}", implementations.size(), classNamesList(implementations));
			this.implementationsIterator = IteratorUtils.loopingIterator(implementations);
		}
	}

	public MatrixAddressDiscoveryServiceImplementation getNextImplementation() {
		return implementationsIterator.next();
	}
}
