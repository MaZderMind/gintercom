package de.mazdermind.gintercom.matrix.integration.tools.builder;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelDeRegistrationEvent;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelRegistrationEvent;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.portpool.PortSet;

@TestComponent
public class PanelRegistrationEventBuilder {
	private static final InetAddress LOOPBACK = InetAddress.getLoopbackAddress();

	@Autowired
	private PortAllocationManager portAllocationManager;


	public PanelRegistrationEvent buildPanelRegistrationEvent(PanelConfig panelConfig) {
		PortSet portSet = portAllocationManager.allocatePortSet(panelConfig.getHostId());

		return new PanelRegistrationEvent(panelConfig.getDisplay(), panelConfig, portSet, LOOPBACK);
	}

	public PanelDeRegistrationEvent buildPanelDeRegistrationEvent(PanelRegistrationEvent panelRegistrationEvent) {
		return new PanelDeRegistrationEvent(panelRegistrationEvent.getPanelId());
	}
}
