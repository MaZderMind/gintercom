package de.mazdermind.gintercom.clientapi.configuration;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ButtonConfig {
	@NotNull
	private String display;

	@NotNull
	private String target;

	@NotNull
	private CommunicationTargetType targetType = CommunicationTargetType.GROUP;

	@NotNull
	private ButtonAction action = ButtonAction.PUSH;

	@NotNull
	private CommunicationDirection direction = CommunicationDirection.TX;

	public boolean usesGroup(String groupId) {
		return targetType == CommunicationTargetType.GROUP && target.equals(groupId);
	}
}
