package de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.mazdermind.gintercom.clientapi.controlserver.messages.wrapper.WrappedClientMessage;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Message sent from the Client to the Matrix to indicate its aliveness.
 */
@Data
@Accessors(chain = true)
@JsonSerialize
public class ClientHeartbeatMessage {
	/**
	 * Inner Message-Class which this Message is wrapped in for distribution within the Matrix.
	 */
	public static class ClientMessage extends WrappedClientMessage<ClientHeartbeatMessage> {
	}
}
