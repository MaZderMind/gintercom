package de.mazdermind.gintercom.mixingcore.tools;

import de.mazdermind.gintercom.mixingcore.Panel;
import de.mazdermind.gintercom.mixingcore.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.tools.rtp.RtpTestClient;

public class PanelAndClient {
	private final Panel panel;
	private final RtpTestClient client;
	private final PortSet ports;

	PanelAndClient(Panel panel, RtpTestClient client, PortSet ports) {
		this.panel = panel;
		this.client = client;
		this.ports = ports;
	}

	public Panel getPanel() {
		return panel;
	}

	public RtpTestClient getClient() {
		return client;
	}

	public void stopAndRemove() {
		client.stop();
		panel.remove();
	}

	public PortSet getPorts() {
		return ports;
	}
}
