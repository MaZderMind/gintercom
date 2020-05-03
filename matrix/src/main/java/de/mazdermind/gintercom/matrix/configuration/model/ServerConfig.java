package de.mazdermind.gintercom.matrix.configuration.model;

import java.net.InetAddress;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ServerConfig {
	@NotNull
	private InetAddress bind;

	@NotNull
	private Integer port;
}
