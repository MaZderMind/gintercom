package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.constraints.NotNull;

public class RtpConfig {
	@NotNull
	private Long jitterbuffer;

	public Long getJitterbuffer() {
		return jitterbuffer;
	}
}
