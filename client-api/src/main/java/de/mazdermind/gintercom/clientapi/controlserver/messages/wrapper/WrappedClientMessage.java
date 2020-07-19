package de.mazdermind.gintercom.clientapi.controlserver.messages.wrapper;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * This Wrapper-Class represents a Message that has been received from a Client that is associated with the Matrix.
 * It combines the Host-ID of the Source-Client to the received Message.
 *
 * @param <T> Type of the received Message.
 */
@Data
@Accessors(chain = true)
public abstract class WrappedClientMessage<T> {
	private T message;
	private String hostId;
}
