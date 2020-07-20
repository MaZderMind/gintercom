package de.mazdermind.gintercom.mixingcore.tools;

import de.mazdermind.gintercom.mixingcore.Client;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.tools.rtp.RtpTestClient;

/**
 * Holds a Reference to the MixingCore Client-Entity as well as the RtpTestClient used to communicate to the Mixing-Core Port.
 */
public class ClientInfo {
	private final MixingCore mixingCore;
	private final Client client;
	private final RtpTestClient rtpTestClient;
	private final PortSet ports;

	ClientInfo(MixingCore mixingCore, Client client, RtpTestClient rtpTestClient, PortSet ports) {
		this.mixingCore = mixingCore;
		this.client = client;
		this.rtpTestClient = rtpTestClient;
		this.ports = ports;
	}

	public Client getClientEntity() {
		return client;
	}

	public RtpTestClient getRtpClient() {
		return rtpTestClient;
	}

	public void stopAndRemove() {
		rtpTestClient.stop();

		if (mixingCore.hasClient(client)) {
			mixingCore.removeClient(client);
		}
	}

	public PortSet getPorts() {
		return ports;
	}
}
