package de.mazdermind.gintercom.clientsupport.controlserver;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.configuration.ClientConfiguration;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociationRequestMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociationRequestMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.AssociatedMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.DeAssociatedMessage;
import de.mazdermind.gintercom.clientsupport.events.AssociatedEvent;
import de.mazdermind.gintercom.clientsupport.events.BeforeShutdownEvent;
import de.mazdermind.gintercom.clientsupport.events.DeAssociatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientAssociationManager {
	private final ControlServerClient controlServerClient;
	private final ClientConfiguration clientConfiguration;
	private final ClientMessageSender clientMessageSender;
	private final ApplicationEventPublisher eventPublisher;
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private InetSocketAddress targetMatrix = null;

	public void initiateAssociation(InetSocketAddress matrixAddress) {
		setupClient(matrixAddress);

		clientMessageSender.sendMessage(new AssociationRequestMessage()
			.setHostId(clientConfiguration.getHostId())
			.setClientModel(clientConfiguration.getClientModel())
			.setCapabilities(new AssociationRequestMessage.Capabilities()
				.setButtons(clientConfiguration.getButtons())));
	}

	private void setupClient(InetSocketAddress matrixAddress) {
		targetMatrix = matrixAddress;
		controlServerClient.start();
		clientMessageSender.setTarget(matrixAddress);
	}

	public void deAssociate(String reason) {
		clientMessageSender.sendMessage(new DeAssociationRequestMessage()
			.setReason(reason));

		teardownClient();
		eventPublisher.publishEvent(new DeAssociatedEvent());
	}

	private void teardownClient() {
		clientMessageSender.setTarget(null);
		controlServerClient.stop();
		targetMatrix = null;
	}

	@EventListener
	public void handleAssociatedMessage(AssociatedMessage associatedMessage) {
		eventPublisher.publishEvent(new AssociatedEvent()
			.setMatrixAddress(targetMatrix)
			.setRtpMatrixToPanelPort(associatedMessage.getRtpMatrixToPanelPort())
			.setRtpPanelToMatrixPort(associatedMessage.getRtpPanelToMatrixPort()));
	}

	@EventListener(DeAssociatedMessage.class)
	public void handleDeAssociatedMessage() {
		// run teardown asynchronously, so that the client can be shutdown even tough the shutdown is triggered by an incoming message
		// (which would otherwise block the socket)
		executorService.submit(this::teardownClient);

		eventPublisher.publishEvent(new DeAssociatedEvent());
	}

	@EventListener(BeforeShutdownEvent.class)
	public void sendDeAssociationMessage() {
		log.info("Notifying Matrix of Shutdown");
		clientMessageSender.sendMessage(new DeAssociationRequestMessage()
			.setReason("Shutdown"));
	}
}
