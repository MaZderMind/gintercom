package de.mazdermind.gintercom.debugclient.controlserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.mazdermind.gintercom.clientsupport.controlserver.discovery.manualconfig.ManualMatrixAddressConfiguration;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.manualconfig.SimpleManualMatrixAddressConfiguration;
import de.mazdermind.gintercom.debugclient.configuration.CliArguments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DebugClientManualMatrixAddressConfiguration {
	private final CliArguments cliArguments;

	@Bean
	public ManualMatrixAddressConfiguration getManualConfiguration() {
		if (cliArguments.getMatrixHost().isPresent() && cliArguments.getMatrixPort().isPresent()) {
			log.info("Using manual Matrix-Address-Configuration from Cli-Arguments");
			return new SimpleManualMatrixAddressConfiguration(
				cliArguments.getMatrixHost().get(),
				cliArguments.getMatrixPort().get()
			);
		}

		log.info("No manual Matrix-Address-Configuration specified on Cli-Arguments");
		return null;
	}
}
