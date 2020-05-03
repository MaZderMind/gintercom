package de.mazdermind.gintercom.clientsupport.controlserver.events;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

import de.mazdermind.gintercom.clientsupport.controlserver.ConnectionLifecycle;

public class AddressDiscoveryEvent extends ConnectionLifecycleEvent {
	private final String implementationId;
	private final String implementationName;

	public AddressDiscoveryEvent(String implementationId, String implementationName) {
		this.implementationId = implementationId;
		this.implementationName = implementationName;
	}

	@Override
	public String getDisplayText() {
		return "Searching for Matrix";
	}

	@Override
	public String getDetailsText() {
		return "using " + implementationName + "…";
	}

	@Override
	public ConnectionLifecycle getLifecycle() {
		return ConnectionLifecycle.DISCOVERY;
	}

	public String getImplementationId() {
		return implementationId;
	}

	public String getImplementationName() {
		return implementationName;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(implementationId, implementationName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AddressDiscoveryEvent that = (AddressDiscoveryEvent) o;
		return Objects.equal(implementationId, that.implementationId) &&
			Objects.equal(implementationName, that.implementationName);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("implementationId", implementationId)
			.append("implementationName", implementationName)
			.toString();
	}
}
