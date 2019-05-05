package de.mazdermind.gintercom.shared.controlserver.provisioning;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

import de.mazdermind.gintercom.shared.configuration.ButtonConfig;

public class ProvisioningInformation {
	@NotNull
	private String display;

	@NotNull
	private Integer matrixToPanelPort;

	@NotNull
	private Integer panelToPanelPort;

	@NotNull
	private Map<String, ButtonConfig> buttons;

	public String getDisplay() {
		return display;
	}

	public ProvisioningInformation setDisplay(String display) {
		this.display = display;
		return this;
	}

	public Integer getMatrixToPanelPort() {
		return matrixToPanelPort;
	}

	public ProvisioningInformation setMatrixToPanelPort(Integer matrixToPanelPort) {
		this.matrixToPanelPort = matrixToPanelPort;
		return this;
	}

	public Integer getPanelToPanelPort() {
		return panelToPanelPort;
	}

	public ProvisioningInformation setPanelToPanelPort(Integer panelToPanelPort) {
		this.panelToPanelPort = panelToPanelPort;
		return this;
	}

	public Map<String, ButtonConfig> getButtons() {
		return buttons;
	}

	public ProvisioningInformation setButtons(Map<String, ButtonConfig> buttons) {
		this.buttons = buttons;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(display, matrixToPanelPort, panelToPanelPort, buttons);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProvisioningInformation that = (ProvisioningInformation) o;
		return Objects.equal(display, that.display) &&
			Objects.equal(matrixToPanelPort, that.matrixToPanelPort) &&
			Objects.equal(panelToPanelPort, that.panelToPanelPort) &&
			Objects.equal(buttons, that.buttons);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("display", display)
			.append("matrixToPanelPort", matrixToPanelPort)
			.append("panelToPanelPort", panelToPanelPort)
			.append("buttons", buttons)
			.toString();
	}
}
