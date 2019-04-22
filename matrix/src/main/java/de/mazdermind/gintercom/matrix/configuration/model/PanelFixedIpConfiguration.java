package de.mazdermind.gintercom.matrix.configuration.model;

import java.net.InetAddress;

import javax.validation.constraints.NotNull;

public class PanelFixedIpConfiguration {
	@NotNull
	private InetAddress ip;

	@NotNull
	private Integer matrixPort;

	@NotNull
	private Integer clientPort;

	public InetAddress getIp() {
		return ip;
	}

	public Integer getMatrixPort() {
		return matrixPort;
	}

	public Integer getClientPort() {
		return clientPort;
	}
}
