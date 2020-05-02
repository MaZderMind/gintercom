package de.mazdermind.gintercom.debugclient.controlserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.mazdermind.gintercom.debugclient.configuration.CliArguments;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.manualconfig.ManualMatrixAddressConfiguration;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.manualconfig.SimpleManualMatrixAddressConfiguration;

@Configuration
public class DebugClientManualMatrixAddressConfiguration {
	private static final Logger log = LoggerFactory.getLogger(DebugClientManualMatrixAddressConfiguration.class);
	private final CliArguments cliArguments;

	public DebugClientManualMatrixAddressConfiguration(
		@Autowired CliArguments cliArguments
	) {
		this.cliArguments = cliArguments;
	}

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
