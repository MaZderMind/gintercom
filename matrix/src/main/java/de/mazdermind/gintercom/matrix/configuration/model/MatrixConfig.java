package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MatrixConfig {
	@NotNull
	private String display;

	@Valid
	@NotNull
	private ServerConfig webui;

	@Valid
	@NotNull
	private RtpConfig rtp;

	@Valid
	@NotNull
	private PortsConfig ports;
}
