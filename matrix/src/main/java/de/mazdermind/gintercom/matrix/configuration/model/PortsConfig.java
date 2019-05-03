package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
}
