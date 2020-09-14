package de.mazdermind.gintercom.matrix.restapi.buttonSets;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.CommunicationTargetType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ButtonDto {
	private String id;
	private String display;
	private CommunicationTargetType targetType;
	private String target;

	public ButtonDto(String id, ButtonConfig config) {
		this.id = id;
		display = config.getDisplay();
		targetType = config.getTargetType();
		target = config.getTarget();
	}
}
