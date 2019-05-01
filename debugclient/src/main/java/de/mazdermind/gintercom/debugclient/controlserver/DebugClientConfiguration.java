package de.mazdermind.gintercom.debugclient.controlserver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import de.mazdermind.gintercom.shared.controlserver.GintercomClientConfiguration;
import de.mazdermind.gintercom.shared.hostid.FileBasedHostId;

@Component
public class DebugClientConfiguration implements GintercomClientConfiguration {
	private final FileBasedHostId fileBasedHostId;

	public DebugClientConfiguration(
		@Autowired FileBasedHostId fileBasedHostId) {
		this.fileBasedHostId = fileBasedHostId;
	}

	@Override
	public String getClientId() {
		return fileBasedHostId.getHostId();
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
		return ImmutableList.of("1", "2", "3", "4", "5", "6");
	}
}
