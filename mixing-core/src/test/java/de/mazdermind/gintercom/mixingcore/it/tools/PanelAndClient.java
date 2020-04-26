package de.mazdermind.gintercom.mixingcore.it.tools;

import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Panel;
import de.mazdermind.gintercom.mixingcore.it.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.it.tools.rtp.RtpTestClient;

public class PanelAndClient {
	private final MixingCore mixingCore;
	private final Panel panel;
	private final RtpTestClient client;
	private final PortSet ports;

	PanelAndClient(MixingCore mixingCore, Panel panel, RtpTestClient client, PortSet ports) {
		this.mixingCore = mixingCore;
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

		if (mixingCore.hasPanel(panel)) {
			mixingCore.removePanel(panel);
		}
	}

	public PortSet getPorts() {
		return ports;
	}
}
