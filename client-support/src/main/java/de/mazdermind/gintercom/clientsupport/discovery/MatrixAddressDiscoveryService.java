package de.mazdermind.gintercom.clientsupport.discovery;

import static de.mazdermind.gintercom.clientsupport.utils.ObjectListClassNameUtil.classNamesList;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.clientsupport.discovery.impl.ManualMatrixAddressConfigurationDiscoveryService;
import de.mazdermind.gintercom.clientsupport.discovery.manualconfig.ManualMatrixAddressConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MatrixAddressDiscoveryService {
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
