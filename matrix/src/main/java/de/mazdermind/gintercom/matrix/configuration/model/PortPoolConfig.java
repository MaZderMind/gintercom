package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PortPoolConfig {
	@Valid
	@NotNull
	private Integer start;

	@Valid
	@NotNull
	private Integer limit;
}
