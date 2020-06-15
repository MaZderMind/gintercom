package de.mazdermind.gintercom.clientsupport.controlserver;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.configuration.ClientConfiguration;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.AssociateMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociateMessage;
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
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private InetSocketAddress targetMatrix = null;

	public void initiateAssociation(InetSocketAddress matrixAddress) {
		setupClient(matrixAddress);

		clientMessageSender.sendMessage(new AssociateMessage()
			.setHostId(clientConfiguration.getHostId())
			.setClientModel(clientConfiguration.getClientModel())
			.setCapabilities(new AssociateMessage.Capabilities()
				.setButtons(clientConfiguration.getButtons())));
	}

	private void setupClient(InetSocketAddress matrixAddress) {
		targetMatrix = matrixAddress;
		controlServerClient.start();
		clientMessageSender.setTarget(matrixAddress);
	}

	public void deAssociate(String reason) {
		clientMessageSender.sendMessage(new DeAssociateMessage()
			.setReason(reason));

		teardownClient();
	}

	private void teardownClient() {
		clientMessageSender.setTarget(null);
		controlServerClient.stop();
		targetMatrix = null;
	}

	@EventListener
	public AssociatedEvent handleAssociatedMessage(AssociatedMessage associatedMessage) {
		return new AssociatedEvent()
			.setMatrixAddress(targetMatrix)
			.setRtpMatrixToPanelPort(associatedMessage.getRtpMatrixToPanelPort())
			.setRtpPanelToMatrixPort(associatedMessage.getRtpPanelToMatrixPort());
	}

	@EventListener(DeAssociatedMessage.class)
	public DeAssociatedEvent handleDeAssociatedMessage() {
		// run teardown asynchronously
		executorService.submit(this::teardownClient);

		return new DeAssociatedEvent();
	}

	@EventListener(BeforeShutdownEvent.class)
	public void sendDeAssociationMessage() {
		log.info("Notifying Matrix of Shutdown");
		clientMessageSender.sendMessage(new DeAssociateMessage()
			.setReason("Shutdown"));
	}
}
