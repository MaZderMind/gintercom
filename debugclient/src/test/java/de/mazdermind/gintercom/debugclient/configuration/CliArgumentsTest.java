package de.mazdermind.gintercom.debugclient.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.springframework.boot.ApplicationArguments;

import com.google.common.collect.ImmutableList;

public class CliArgumentsTest {

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	private CliArguments cliArguments;
	private List<String> sourceArgs = Collections.emptyList();

	@Before
	public void prepare() {
		ApplicationArguments applicationArguments = mock(ApplicationArguments.class);
		when(applicationArguments.getSourceArgs()).thenAnswer(invocation -> sourceArgs.toArray(new String[0]));
		cliArguments = new CliArguments(applicationArguments);
	}

	@Test
	public void parsesEmptyConfig() throws ParseException {
		cliArguments.parseArgs();

		assertThat(cliArguments.hasManualMatrixAddress()).isFalse();
		assertThat(cliArguments.getMatrixPort()).isEmpty();
		assertThat(cliArguments.getMatrixHost()).isEmpty();
		assertThat(cliArguments.getButtons()).isEmpty();
		assertThat(cliArguments.getClientId()).isEmpty();
	}

	@Test
	public void parsesMatrixAndPort() throws ParseException, UnknownHostException {
		sourceArgs = ImmutableList.of("--host", "10.219.42.32", "--port", "9999");
		cliArguments.parseArgs();

		assertThat(cliArguments.hasManualMatrixAddress()).isTrue();
		assertThat(cliArguments.getMatrixHost()).hasValue(InetAddress.getByName("10.219.42.32"));
		assertThat(cliArguments.getMatrixPort()).hasValue(9999);
	}

	@Test
	public void failsOnAddressWithoutPort() throws ParseException {
		exit.expectSystemExitWithStatus(1);

		sourceArgs = ImmutableList.of("--host", "10.219.42.32");
		cliArguments.parseArgs();
	}

	@Test
	public void failsOnPortWithoutAddress() throws ParseException {
		exit.expectSystemExitWithStatus(1);

		sourceArgs = ImmutableList.of("--port", "9999");
		cliArguments.parseArgs();
	}

	@Test
	public void parsesClientId() throws ParseException {
		sourceArgs = ImmutableList.of("--client-id", "FOO:BAR");
		cliArguments.parseArgs();

		assertThat(cliArguments.getClientId()).hasValue("FOO:BAR");
	}

	@Test
	public void parsesButtonList() throws ParseException {
		sourceArgs = ImmutableList.of("--buttons", " 1,2,,3 , X1 ,X2,,Reply ");
		cliArguments.parseArgs();

		assertThat(cliArguments.getButtons()).hasValue(ImmutableList.of(
			"1", "2", "3", "X1", "X2", "Reply"
		));
	}
}
