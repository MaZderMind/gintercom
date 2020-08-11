package de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix;

import javax.validation.constraints.NotNull;

import de.mazdermind.gintercom.clientapi.configuration.CommunicationTargetType;
import de.mazdermind.gintercom.clientapi.controlserver.messages.wrapper.WrappedClientMessage;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MembershipChangeMessage {
	@NotNull
	private Change change;

	@NotNull
	private String target;

	@NotNull
	private CommunicationTargetType targetType = CommunicationTargetType.GROUP;

	public enum Change {
		JOIN,
		LEAVE
	}

	/**
	 * Inner Message-Class which this Message is wrapped in for distribution within the Matrix.
	 */
	public static class ClientMessage extends WrappedClientMessage<MembershipChangeMessage> {
	}
}
