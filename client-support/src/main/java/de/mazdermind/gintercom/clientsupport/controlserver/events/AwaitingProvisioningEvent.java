package de.mazdermind.gintercom.clientsupport.controlserver.events;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

import de.mazdermind.gintercom.clientsupport.controlserver.ConnectionLifecycle;

public class AwaitingProvisioningEvent extends ConnectionLifecycleEvent {
	private final String hostId;

	public AwaitingProvisioningEvent(String hostId) {
		this.hostId = hostId;
	}

	@Override
	public String getDisplayText() {
		return "Awaiting Provisioning";
	}

	@Override
	public String getDetailsText() {
		return "Host-ID: " + hostId;
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.PROVISIONING;
	}

	public String getHostId() {
		return hostId;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hostId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AwaitingProvisioningEvent that = (AwaitingProvisioningEvent) o;
		return Objects.equal(hostId, that.hostId);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("hostId", hostId)
			.toString();
	}
}
