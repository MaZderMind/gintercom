package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.PreDestroy;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.portpool.PortAllocationManager;
import de.mazdermind.gintercom.matrix.portpool.PortSet;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AssociatedClientsManager {
	private final PortAllocationManager portAllocationManager;
	private final ApplicationEventPublisher eventPublisher;

	private final AssociatedClientsStore associations = new AssociatedClientsStore();

	@VisibleForTesting
	public ClientAssociation associate(InetSocketAddress address, String hostId) {
		if (associations.isAssociated(hostId)) {
			throw new HostIdAlreadyAssociatedException(hostId);
		} else if (associations.isAssociated(address)) {
			throw new SocketAddressAlreadyAssociatedException(address);
		}

		PortSet portSet = portAllocationManager.allocatePortSet(hostId);

		ClientAssociation association = associations.associate(new ClientAssociation()
			.setHostId(hostId)
			.setSocketAddress(address)
			.setRtpPorts(portSet));

		eventPublisher.publishEvent(new ClientAssociatedEvent()
			.setAssociation(association));

		return association;
	}

	void deAssociate(String hostId) {
		ClientAssociation association = getAssociation(hostId);

		eventPublisher.publishEvent(new ClientDeAssociatedEvent()
			.setAssociation(association));

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
		associations.getAssociations().forEach(this::deAssociate);
	}

	public void deAssociate(ClientAssociation clientAssociation) {
		deAssociate(clientAssociation.getHostId());
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
