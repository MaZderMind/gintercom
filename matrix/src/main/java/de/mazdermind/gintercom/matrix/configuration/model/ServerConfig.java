package de.mazdermind.gintercom.matrix.configuration.model;

import java.net.InetAddress;

import javax.validation.constraints.NotNull;

public class ServerConfig {
	@NotNull
	private InetAddress bind;

	@NotNull
	private Integer port;

	public InetAddress getBind() {
		return bind;
	}

	public Integer getPort() {
		return port;
	}
}
