package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PortPoolConfig {
	@Valid
	@NotNull
	private Integer start;

	@Valid
	@NotNull
	private Integer limit;

	public Integer getStart() {
		return start;
	}

	public Integer getLimit() {
		return limit;
	}
}
