package de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix;

import javax.validation.constraints.NotEmpty;

import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.wrapper.WrappedClientMessage;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * An DeAssociationRequestMessage is sent from a Client to Matrix to request de-association from it. Usually the Matrix
 * responds with an {@link DeAssociatedMessage}.
 */
@Data
@Accessors(chain = true)
public class DeAssociationRequestMessage {
	@NotEmpty
	private String reason;

	/**
	 * Inner Message-Class which this Message is wrapped in for distribution within the Matrix.
	 */
	public static class ClientMessage extends WrappedClientMessage<DeAssociationRequestMessage> {
	}
}
