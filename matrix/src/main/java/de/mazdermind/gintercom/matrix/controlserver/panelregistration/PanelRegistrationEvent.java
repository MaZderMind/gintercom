package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import java.net.InetAddress;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.portpool.PortSet;

public class PanelRegistrationEvent {
	private String panelId;
	private PanelConfig panelConfig;
	private PortSet portSet;
	private InetAddress hostAddress;

	public PanelRegistrationEvent(String panelId, PanelConfig panelConfig, PortSet portSet, InetAddress hostAddress) {
		this.panelId = panelId;
		this.panelConfig = panelConfig;
		this.portSet = portSet;
		this.hostAddress = hostAddress;
	}

	public String getPanelId() {
		return panelId;
	}

	public PanelConfig getPanelConfig() {
		return panelConfig;
	}

	public PortSet getPortSet() {
		return portSet;
	}

	public InetAddress getHostAddress() {
		return hostAddress;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(panelId, panelConfig, portSet, hostAddress);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PanelRegistrationEvent that = (PanelRegistrationEvent) o;
		return Objects.equal(panelId, that.panelId) &&
			Objects.equal(panelConfig, that.panelConfig) &&
			Objects.equal(portSet, that.portSet) &&
			Objects.equal(hostAddress, that.hostAddress);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("panelId", panelId)
			.append("panelConfig", panelConfig)
			.append("portSet", portSet)
			.append("hostAddress", hostAddress)
			.toString();
	}
}
