package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PortsConfig {
	@Valid
	@NotNull
	private PortPoolConfig clientToMatrix;

	@Valid
	@NotNull
	private PortPoolConfig matrixToClient;
}
