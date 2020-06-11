package de.mazdermind.gintercom.matrix.events;

import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ClientDeAssociatedEvent {
	private ClientAssociation association;
	private String reason;
}
