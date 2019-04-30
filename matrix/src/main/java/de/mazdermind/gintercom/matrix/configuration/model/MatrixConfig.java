package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class MatrixConfig {
	@NotNull
	private String display;

	@Valid
	@NotNull
	private ServerConfig webui;

	@Valid
	@NotNull
	private RtpConfig rtp;

	public String getDisplay() {
		return display;
	}

	public ServerConfig getWebui() {
		return webui;
	}

	public RtpConfig getRtp() {
		return rtp;
	}

}
