package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

public class PortsConfig {
	@Valid
	@NotNull
	private PortPoolConfig panelToMatrix;

	@Valid
	@NotNull
	private PortPoolConfig matrixToPanel;

	public PortPoolConfig getPanelToMatrix() {
		return panelToMatrix;
	}

	public PortPoolConfig getMatrixToPanel() {
		return matrixToPanel;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(panelToMatrix, matrixToPanel);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PortsConfig that = (PortsConfig) o;
		return Objects.equal(panelToMatrix, that.panelToMatrix) &&
			Objects.equal(matrixToPanel, that.matrixToPanel);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("panelToMatrix", panelToMatrix)
			.append("matrixToPanel", matrixToPanel)
			.toString();
	}
}
