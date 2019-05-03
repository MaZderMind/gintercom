package de.mazdermind.gintercom.debugclient.controlserver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.debugclient.configuration.CliArguments;
import de.mazdermind.gintercom.shared.controlserver.ClientConfiguration;
import de.mazdermind.gintercom.shared.hostid.FileBasedHostId;

@Configuration
public class DebugClientConfiguration implements ClientConfiguration {
	private final FileBasedHostId fileBasedHostId;
	private final CliArguments cliArguments;

	public DebugClientConfiguration(
		@Autowired FileBasedHostId fileBasedHostId,
		@Autowired CliArguments cliArguments
	) {
		this.fileBasedHostId = fileBasedHostId;
		this.cliArguments = cliArguments;
	}

	@Override
	public String getClientId() {
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
