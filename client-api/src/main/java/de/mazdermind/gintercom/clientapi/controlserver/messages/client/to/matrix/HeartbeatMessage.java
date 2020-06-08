package de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.mazdermind.gintercom.clientapi.controlserver.messages.wrapper.WrappedClientMessage;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonSerialize
public class HeartbeatMessage {
	/**
	 * Inner Message-Class which this Message is wrapped in before being distributed within the Matrix.
	 */
	public static class ClientMessage extends WrappedClientMessage<HeartbeatMessage> {
	}
}
