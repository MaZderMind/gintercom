package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import java.net.InetAddress;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.portpool.PortSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class PanelRegistrationEvent {
	private String panelId;
	private PanelConfig panelConfig;
	private PortSet portSet;
	private InetAddress hostAddress;
}
