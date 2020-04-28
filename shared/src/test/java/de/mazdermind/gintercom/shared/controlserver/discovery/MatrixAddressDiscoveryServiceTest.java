package de.mazdermind.gintercom.shared.controlserver.discovery;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.shared.controlserver.discovery.impl.ManualMatrixAddressConfigurationDiscoveryService;
import de.mazdermind.gintercom.shared.controlserver.discovery.manualconfig.SimpleManualMatrixAddressConfiguration;

public class MatrixAddressDiscoveryServiceTest {

	private MatrixAddressDiscoveryServiceImplementation impl1;
	private MatrixAddressDiscoveryServiceImplementation impl2;
	private SimpleManualMatrixAddressConfiguration manualConfig;

	@Before
	public void prepare() throws UnknownHostException {
		impl1 = mock(MatrixAddressDiscoveryServiceImplementation.class);
		impl2 = mock(MatrixAddressDiscoveryServiceImplementation.class);
		manualConfig = new SimpleManualMatrixAddressConfiguration(InetAddress.getByName("10.73.42.23"), 42);
	}

	@Test
	public void loopsOverAvailableDiscoveryMethods() {
		MatrixAddressDiscoveryService discoveryService = new MatrixAddressDiscoveryService(
			ImmutableList.of(impl1, impl2), Optional.empty());

		assertThat(discoveryService.getNextImplementation()).isSameAs(impl1);
		assertThat(discoveryService.getNextImplementation()).isSameAs(impl2);
		assertThat(discoveryService.getNextImplementation()).isSameAs(impl1);
		assertThat(discoveryService.getNextImplementation()).isSameAs(impl2);
	}

	@Test
	public void worksWithOnlyOneDiscoveryMethod() {
		MatrixAddressDiscoveryService discoveryService = new MatrixAddressDiscoveryService(
			ImmutableList.of(impl1), Optional.empty());

		assertThat(discoveryService.getNextImplementation()).isSameAs(impl1);
		assertThat(discoveryService.getNextImplementation()).isSameAs(impl1);
		assertThat(discoveryService.getNextImplementation()).isSameAs(impl1);
	}

	@Test(expected = AssertionError.class)
	public void failsWithoutDiscoveryMethods() {
		new MatrixAddressDiscoveryService(Collections.emptyList(), Optional.empty());
	}

	@Test
	public void onlyUsesManualConfigurationIfAvailable() {
		MatrixAddressDiscoveryService discoveryService = new MatrixAddressDiscoveryService(
			ImmutableList.of(impl1, impl2), Optional.of(manualConfig));

		assertThat(discoveryService.getNextImplementation()).isInstanceOf(ManualMatrixAddressConfigurationDiscoveryService.class);
		assertThat(discoveryService.getNextImplementation()).isInstanceOf(ManualMatrixAddressConfigurationDiscoveryService.class);
		assertThat(discoveryService.getNextImplementation()).isInstanceOf(ManualMatrixAddressConfigurationDiscoveryService.class);

		MatrixAddressDiscoveryServiceImplementation nextImplementation = discoveryService.getNextImplementation();

		Optional<MatrixAddressDiscoveryServiceResult> discoveryResult = nextImplementation.tryDiscovery();
		assertThat(discoveryResult.isPresent()).isTrue();
		assertThat(discoveryResult.get().getAddress()).isEqualTo(manualConfig.getAddress());
		assertThat(discoveryResult.get().getPort()).isEqualTo(manualConfig.getPort());
	}
}
