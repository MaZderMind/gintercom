package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class PanelDeRegistrationEvent {
	private String panelId;

	public PanelDeRegistrationEvent(String panelId) {
		this.panelId = panelId;
	}

	public String getPanelId() {
		return panelId;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(panelId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PanelDeRegistrationEvent that = (PanelDeRegistrationEvent) o;
		return Objects.equal(panelId, that.panelId);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("panelId", panelId)
			.toString();
	}
}

