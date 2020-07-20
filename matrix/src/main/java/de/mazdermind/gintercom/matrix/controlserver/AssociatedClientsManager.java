package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociationRequestMessage;
import de.mazdermind.gintercom.matrix.events.BeforeMatrixShutdownEvent;
import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.portpool.PortSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssociatedClientsManager {
	private final PortAllocationManager portAllocationManager;
	private final ApplicationEventPublisher eventPublisher;

	private final AssociatedClientsStore associations = new AssociatedClientsStore();

	public ClientAssociation associate(InetSocketAddress address, String clientId, String clientModel) {
		if (associations.isAssociated(clientId)) {
			log.warn("Rejecting Association-Request from {} because the Client-Id {} is already associated", address, clientId);
			throw new ClientIdAlreadyAssociatedException(clientId);
		} else if (associations.isAssociated(address)) {
			log.warn("Rejecting Association-Request from {} because this SocketAddress is already associated", address);
			throw new SocketAddressAlreadyAssociatedException(address);
		}

		log.info("Associating Address {} with the Matrix as Client-Id {}", address, clientId);

		PortSet portSet = portAllocationManager.allocatePortSet(clientId);

		ClientAssociation association = associations.associate(new ClientAssociation()
			.setClientId(clientId)
			.setClientModel(clientModel)
			.setSocketAddress(address)
			.setRtpPorts(portSet));

		eventPublisher.publishEvent(new ClientAssociatedEvent()
			.setAssociation(association));

		return association;
	}

	@EventListener
	public void handleDeAssociationRequestMessage(DeAssociationRequestMessage.ClientMessage deAssociationRequestMessage) {
		log.info("Received De-Association-Request from Client-Id {}", deAssociationRequestMessage.getClientId());

		String reason = String.format(
			"Received DeAssociationRequestMessage with reason: '%s'",
			deAssociationRequestMessage.getMessage().getReason());

		deAssociate(deAssociationRequestMessage.getClientId(), reason);
	}

	public void deAssociate(String clientId, String reason) {
		deAssociate(getAssociation(clientId), reason);
	}

	public void deAssociate(ClientAssociation association, String reason) {
		log.info("De-Associating Address {} from the Matrix (was associated as Client-Id {}) for Reason: {}",
			association.getSocketAddress(), association.getClientId(), reason);

		eventPublisher.publishEvent(new ClientDeAssociatedEvent()
			.setAssociation(association)
			.setReason(reason));

		associations.deAssociate(association.getClientId());
	}

	/**
	 * Returns the Association or Fails with an Exception if not Association could be found.
	 *
	 * @param clientId Client-Id to get the Association for
	 * @return found Association
	 */
	public ClientAssociation getAssociation(String clientId) {
		return associations.findAssociation(clientId)
			.orElseThrow(() -> new NotAssociatedException(clientId));
	}

	/**
	 * Returns the Association or Fails with an Exception if not Association could be found.
	 *
	 * @param address Socket-Address to get the Association for
	 * @return found Association
	 */
	public ClientAssociation getAssociation(InetSocketAddress address) {
		return associations.findAssociation(address)
			.orElseThrow(() -> new NotAssociatedException(address));
	}

	/**
	 * Returns the Association if known
	 *
	 * @param clientId Client-Id to find the Association for
	 * @return found Association
	 */
	public Optional<ClientAssociation> findAssociation(String clientId) {
		return associations.findAssociation(clientId);
	}

	/**
	 * Returns the Association if known
	 *
	 * @param address Socket-Address to find the Association for
	 * @return found Association
	 */
	public Optional<ClientAssociation> findAssociation(InetSocketAddress address) {
		return associations.findAssociation(address);
	}

	public boolean isAssociated(String clientId) {
		return associations.isAssociated(clientId);
	}

	public boolean isAssociated(InetSocketAddress address) {
		return associations.isAssociated(address);
	}

	public Collection<ClientAssociation> getAssociations() {
		return associations.getAssociations();
	}

	@EventListener(BeforeMatrixShutdownEvent.class)
	public void deAssociateAll() {
		log.info("DeAssociating all Clients");
		associations.getAssociations().forEach(clientAssociation -> deAssociate(clientAssociation, "Shutdown"));
	}

	public static class ClientIdAlreadyAssociatedException extends RuntimeException {
		public ClientIdAlreadyAssociatedException(String clientId) {
			super(String.format("Client-Id %s is already associated", clientId));
		}
	}

	public static class SocketAddressAlreadyAssociatedException extends RuntimeException {
		public SocketAddressAlreadyAssociatedException(InetSocketAddress address) {
			super(String.format("Socket-Address %s is already associated", address));
		}
	}

	public static class NotAssociatedException extends RuntimeException {
		public NotAssociatedException(String clientId) {
			super(String.format("Client-Id %s not associated", clientId));
		}

		public NotAssociatedException(InetSocketAddress address) {
			super(String.format("Socket-Address %s not associated", address));
		}
	}
}
