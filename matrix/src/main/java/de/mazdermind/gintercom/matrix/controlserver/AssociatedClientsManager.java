package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.PreDestroy;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.DeAssociationRequestMessage;
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

	public ClientAssociation associate(InetSocketAddress address, String hostId, String clientModel) {
		if (associations.isAssociated(hostId)) {
			log.warn("Rejecting Association-Request from {} because the Host-ID {} is already associated", address, hostId);
			throw new HostIdAlreadyAssociatedException(hostId);
		} else if (associations.isAssociated(address)) {
			log.warn("Rejecting Association-Request from {} because this SocketAddress is already associated", address);
			throw new SocketAddressAlreadyAssociatedException(address);
		}

		log.info("Associating Address {} with the Matrix as Host-ID {}", address, hostId);

		PortSet portSet = portAllocationManager.allocatePortSet(hostId);

		ClientAssociation association = associations.associate(new ClientAssociation()
			.setHostId(hostId)
			.setClientModel(clientModel)
			.setSocketAddress(address)
			.setRtpPorts(portSet));

		eventPublisher.publishEvent(new ClientAssociatedEvent()
			.setAssociation(association));

		return association;
	}

	@EventListener
	public void handleDeAssociationRequestMessage(DeAssociationRequestMessage.ClientMessage deAssociationRequestMessage) {
		log.info("Received De-Association-Request from Host-ID {}", deAssociationRequestMessage.getHostId());

		String reason = String.format(
			"Received DeAssociationRequestMessage with reason: '%s'",
			deAssociationRequestMessage.getMessage().getReason());

		deAssociate(deAssociationRequestMessage.getHostId(), reason);
	}

	public void deAssociate(ClientAssociation clientAssociation, String reason) {
		deAssociate(clientAssociation.getHostId(), reason);
	}

	public void deAssociate(String hostId, String reason) {
		ClientAssociation association = getAssociation(hostId);

		log.info("De-Associating Address {} from the Matrix (was associated as Host-ID {}) for Reason: {}",
			association.getSocketAddress(), hostId, reason);

		eventPublisher.publishEvent(new ClientDeAssociatedEvent()
			.setAssociation(association)
			.setReason(reason));

		associations.deAssociate(hostId);
	}

	/**
	 * Returns the Association or Fails with an Exception if not Association could be found.
	 *
	 * @param hostId Host-ID to get the Association for
	 * @return found Association
	 */
	public ClientAssociation getAssociation(String hostId) {
		return associations.findAssociation(hostId)
			.orElseThrow(() -> new NotAssociatedException(hostId));
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
	 * @param hostId Host-ID to find the Association for
	 * @return found Association
	 */
	public Optional<ClientAssociation> findAssociation(String hostId) {
		return associations.findAssociation(hostId);
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

	public boolean isAssociated(String hostId) {
		return associations.isAssociated(hostId);
	}

	public boolean isAssociated(InetSocketAddress address) {
		return associations.isAssociated(address);
	}

	public Collection<ClientAssociation> getAssociations() {
		return associations.getAssociations();
	}

	@PreDestroy
	public void deAssociateAll() {
		associations.getAssociations().forEach(clientAssociation -> deAssociate(clientAssociation, "Shutdown"));
	}

	public static class HostIdAlreadyAssociatedException extends RuntimeException {
		public HostIdAlreadyAssociatedException(String hostId) {
			super(String.format("Host-Id %s is already associated", hostId));
		}
	}

	public static class SocketAddressAlreadyAssociatedException extends RuntimeException {
		public SocketAddressAlreadyAssociatedException(InetSocketAddress address) {
			super(String.format("Socket-Address %s is already associated", address));
		}
	}

	public static class NotAssociatedException extends RuntimeException {
		public NotAssociatedException(String hostId) {
			super(String.format("Host-Id %s not associated", hostId));
		}

		public NotAssociatedException(InetSocketAddress address) {
			super(String.format("Socket-Address %s not associated", address));
		}
	}
}
