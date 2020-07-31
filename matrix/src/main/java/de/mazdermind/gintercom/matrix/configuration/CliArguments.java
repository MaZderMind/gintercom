package de.mazdermind.gintercom.matrix.configuration;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CliArguments {
	private String configDirectory;
}
