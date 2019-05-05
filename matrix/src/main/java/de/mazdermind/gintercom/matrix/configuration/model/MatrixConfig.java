package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

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

	public String getDisplay() {
		return display;
	}

	public ServerConfig getWebui() {
		return webui;
	}

	public RtpConfig getRtp() {
		return rtp;
	}

	public PortsConfig getPorts() {
		return ports;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(display, webui, rtp, ports);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MatrixConfig that = (MatrixConfig) o;
		return Objects.equal(display, that.display) &&
			Objects.equal(webui, that.webui) &&
			Objects.equal(rtp, that.rtp) &&
			Objects.equal(ports, that.ports);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("display", display)
			.append("webui", webui)
			.append("rtp", rtp)
			.append("ports", ports)
			.toString();
	}
}
