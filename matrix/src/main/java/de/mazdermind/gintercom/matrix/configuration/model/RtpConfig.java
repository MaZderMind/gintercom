package de.mazdermind.gintercom.matrix.configuration.model;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RtpConfig {
	@NotNull
	private Long jitterbuffer;
}
