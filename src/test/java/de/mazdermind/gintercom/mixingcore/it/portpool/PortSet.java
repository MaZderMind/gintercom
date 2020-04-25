package de.mazdermind.gintercom.mixingcore.it.portpool;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.base.Objects;

public class PortSet {
	private final int matrixToPanel;
	private final int panelToMatrix;

	public PortSet(int matrixToPanel, int panelToMatrix) {
		this.matrixToPanel = matrixToPanel;
		this.panelToMatrix = panelToMatrix;
	}

	public int getMatrixToPanel() {
		return matrixToPanel;
	}

	public int getPanelToMatrix() {
		return panelToMatrix;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(matrixToPanel, panelToMatrix);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PortSet portSet = (PortSet) o;
		return matrixToPanel == portSet.matrixToPanel &&
				panelToMatrix == portSet.panelToMatrix;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
				.append("matrixToPanel", matrixToPanel)
				.append("panelToMatrix", panelToMatrix)
				.toString();
	}
}
