package de.mazdermind.gintercom.mixingcore.portpool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class PortSet {
	private int matrixToClient;
	private int clientToMatrix;
}
