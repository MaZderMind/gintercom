package de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeAssociatedMessage {
	@NotEmpty
	private String reason;
}
