package de.mazdermind.gintercom.shared.controlserver.discovery;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
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

		assertThat(discoveryService.getNextImplementation(), is(impl1));
		assertThat(discoveryService.getNextImplementation(), is(impl2));
		assertThat(discoveryService.getNextImplementation(), is(impl1));
		assertThat(discoveryService.getNextImplementation(), is(impl2));
	}

	@Test
	public void worksWithOnlyOneDiscoveryMethod() {
		MatrixAddressDiscoveryService discoveryService = new MatrixAddressDiscoveryService(
			ImmutableList.of(impl1), Optional.empty());

		assertThat(discoveryService.getNextImplementation(), is(impl1));
		assertThat(discoveryService.getNextImplementation(), is(impl1));
		assertThat(discoveryService.getNextImplementation(), is(impl1));
	}

	@Test(expected = AssertionError.class)
	public void failsWithoutDiscoveryMethods() {
		new MatrixAddressDiscoveryService(Collections.emptyList(), Optional.empty());
	}

	@Test
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public void onlyUsesManualConfigurationIfAvailable() {
		MatrixAddressDiscoveryService discoveryService = new MatrixAddressDiscoveryService(
			ImmutableList.of(impl1, impl2), Optional.of(manualConfig));

		assertThat(discoveryService.getNextImplementation(), instanceOf(ManualMatrixAddressConfigurationDiscoveryService.class));
		assertThat(discoveryService.getNextImplementation(), instanceOf(ManualMatrixAddressConfigurationDiscoveryService.class));
		assertThat(discoveryService.getNextImplementation(), instanceOf(ManualMatrixAddressConfigurationDiscoveryService.class));

		MatrixAddressDiscoveryServiceImplementation nextImplementation = discoveryService.getNextImplementation();

		Optional<MatrixAddressDiscoveryServiceResult> discoveryResult = nextImplementation.tryDiscovery();
		assertThat(discoveryResult.isPresent(), is(true));
		assertThat(discoveryResult.get().getAddress(), is(manualConfig.getAddress()));
		assertThat(discoveryResult.get().getPort(), is(manualConfig.getPort()));
	}
}
