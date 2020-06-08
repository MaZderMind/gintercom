package de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix;

import javax.validation.constraints.NotEmpty;

import de.mazdermind.gintercom.clientapi.controlserver.messages.wrapper.WrappedClientMessage;
import lombok.Data;
import lombok.experimental.Accessors;

// TODO replace (in Tests & Documentation) with something useful
@Data
@Accessors(chain = true)
public class ExampleMessage {
	@NotEmpty
	private String text;

	/**
	 * Inner Message-Class which this Message is wrapped in before being distributed within the Matrix.
	 */
	public static class ClientMessage extends WrappedClientMessage<ExampleMessage> {
	}
}
