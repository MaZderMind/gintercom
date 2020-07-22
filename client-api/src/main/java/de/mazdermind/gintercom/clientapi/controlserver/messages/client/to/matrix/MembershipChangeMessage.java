package de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix;

import javax.validation.constraints.NotNull;

import de.mazdermind.gintercom.clientapi.configuration.ButtonDirection;
import de.mazdermind.gintercom.clientapi.configuration.ButtonTargetType;
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
	private ButtonTargetType targetType = ButtonTargetType.GROUP;

	@NotNull
	private ButtonDirection direction = ButtonDirection.TX;

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
