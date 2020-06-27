package de.mazdermind.gintercom.matrix.events;

import java.util.Set;

import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * This Event is emitted whenever the Groups a Panel soud transmit to or receive from changed,
 * either through a Button-Press or through a Configuration change.
 */
@Data
@Accessors(chain = true)
public class PanelGroupsChangedEvent {
	private ClientAssociation association;

	private Set<String> rxGroups;
	private Set<String> txGroups;
}
