package de.mazdermind.gintercom.debugclient.controlserver;

import java.util.List;

import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.clientapi.configuration.ClientConfiguration;
import de.mazdermind.gintercom.clientsupport.clientid.FileBasedClientId;
import de.mazdermind.gintercom.debugclient.configuration.CliArguments;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DebugClientConfiguration implements ClientConfiguration {
	private final FileBasedClientId fileBasedClientId;
	private final CliArguments cliArguments;

	@Override
	public String getClientId() {
		return cliArguments.getClientId().orElseGet(fileBasedClientId::getClientId);
	}

	@Override
	public String getClientModel() {
		return "debug-client";
	}

	@Override
	public List<String> getButtons() {
		return cliArguments.getButtons().orElse(ImmutableList.of("1", "2", "3", "4", "5", "6"));
	}
}
