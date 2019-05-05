package de.mazdermind.gintercom.matrix.configuration.model;

import java.net.InetAddress;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hashCode(bind, port);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServerConfig that = (ServerConfig) o;
		return Objects.equal(bind, that.bind) &&
			Objects.equal(port, that.port);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("bind", bind)
			.append("port", port)
			.toString();
	}
}
