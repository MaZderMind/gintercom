package de.mazdermind.gintercom.mixingcore.it.portpool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class PortSet {
	private int matrixToPanel;
	private int panelToMatrix;
}
