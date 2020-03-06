package de.mazdermind.gintercom.mixingcore.tools;

import de.mazdermind.gintercom.mixingcore.Panel;
import de.mazdermind.gintercom.mixingcore.tools.rtp.RtpTestClient;

public class PanelAndClient {
	private final Panel panel;
	private final RtpTestClient client;

	PanelAndClient(Panel panel, RtpTestClient client) {
		this.panel = panel;
		this.client = client;
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
}
