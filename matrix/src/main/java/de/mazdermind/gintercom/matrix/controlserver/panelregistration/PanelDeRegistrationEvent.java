package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PanelDeRegistrationEvent {
	private final String panelId;
}

