package de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.wrapper.WrappedClientMessage;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * An AssociateMessage is sent from a Client to Matrix to request association with it. Usually the Matrix
 * responds with an {@link AssociatedMessage}.
 */
@Data
@Accessors(chain = true)
public class AssociateMessage {
	@NotEmpty
	private String hostId;

	@NotNull
	@Valid
	private Capabilities capabilities = new Capabilities();

	@Data
	@Accessors(chain = true)
	public static class Capabilities {
		@NotNull
		private List<String> buttons = new ArrayList<>();
	}

	/**
	 * Inner Message-Class which this Message is wrapped in for distribution within the Matrix.
	 */
	public static class ClientMessage extends WrappedClientMessage<AssociateMessage> {
	}
}
