package de.mazdermind.gintercom.matrix.events;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import de.mazdermind.gintercom.matrix.portpool.PortSet;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PanelAssociatedEvent {
	private ClientAssociation association;
	private PortSet portSet;
	private String panelId;
	private PanelConfig panelConfig;
}
