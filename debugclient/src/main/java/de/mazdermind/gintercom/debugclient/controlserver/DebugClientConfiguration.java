package de.mazdermind.gintercom.debugclient.controlserver;

import java.util.List;

import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.clientapi.configuration.ClientConfiguration;
import de.mazdermind.gintercom.clientsupport.hostid.FileBasedHostId;
import de.mazdermind.gintercom.debugclient.configuration.CliArguments;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DebugClientConfiguration implements ClientConfiguration {
	private final FileBasedHostId fileBasedHostId;
	private final CliArguments cliArguments;

	@Override
	public String getHostId() {
		return cliArguments.getHostId().orElseGet(fileBasedHostId::getHostId);
	}

	@Override
	public Integer getProtocolVersion() {
		return 1;
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
