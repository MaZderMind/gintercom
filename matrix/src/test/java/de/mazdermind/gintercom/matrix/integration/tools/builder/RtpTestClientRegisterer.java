package de.mazdermind.gintercom.matrix.integration.tools.builder;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelDeRegistrationEvent;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelRegistrationEvent;
import de.mazdermind.gintercom.matrix.integration.tools.rtp.RtpTestClient;
import de.mazdermind.gintercom.matrix.pipeline.Pipeline;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.portpool.PortSet;

@TestComponent
public class RtpTestClientRegisterer {
	private static final Logger log = LoggerFactory.getLogger(RtpTestClientRegisterer.class);
	private static final InetAddress LOOPBACK = InetAddress.getLoopbackAddress();

	@Autowired
	private PortAllocationManager portAllocationManager;

	@Autowired
	private Pipeline pipeline;

	private List<RtpTestClient> registeredTestClients = new ArrayList<>();

	public RtpTestClient registerTestClient(PanelConfig panelConfig) {
		PortSet portSet = portAllocationManager.allocatePortSet(panelConfig.getHostId());

		PanelRegistrationEvent registrationEvent = new PanelRegistrationEvent(
			panelConfig.getDisplay(), panelConfig, portSet, LOOPBACK);

		pipeline.handlePanelRegistration(registrationEvent);

		RtpTestClient rtpTestClient = new RtpTestClient(portSet, panelConfig.getDisplay());
		registeredTestClients.add(rtpTestClient);
		return rtpTestClient;
	}

	public void deregisterTestClient(RtpTestClient testClient) {
		PanelDeRegistrationEvent deRegistrationEvent = new PanelDeRegistrationEvent(testClient.getPanelId());
		pipeline.handlePanelDeRegistration(deRegistrationEvent);
		registeredTestClients.remove(testClient);
	}

	public void deregisterTestClients(RtpTestClient... testClients) {
		Arrays.asList(testClients).forEach(this::deregisterTestClient);
	}

	public void stopAndDeregisterTestClient(RtpTestClient testClient) {
		testClient.stop();
		deregisterTestClient(testClient);
	}

	public void stopAndDeregisterTestClients(RtpTestClient... testClients) {
		Arrays.asList(testClients).forEach(this::stopAndDeregisterTestClient);
	}

	public void stopAndDeregisterAllTestClients() {
		ArrayList<RtpTestClient> clients = new ArrayList<>(registeredTestClients);
		clients.forEach(testClient -> {
			log.info("Stopping & Deregistering {}", testClient.getPanelId());
			stopAndDeregisterTestClient(testClient);
		});
	}
}
