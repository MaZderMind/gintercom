package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AssociatedClientsStore {
	private final Map<String, ClientAssociation> byHostId = new ConcurrentHashMap<>();
	private final Map<InetSocketAddress, ClientAssociation> bySocketAddress = new ConcurrentHashMap<>();

	public ClientAssociation associate(ClientAssociation clientAssociation) {
		byHostId.put(clientAssociation.getHostId(), clientAssociation);
		bySocketAddress.put(clientAssociation.getSocketAddress(), clientAssociation);
		return clientAssociation;
	}

	public ClientAssociation deAssociate(String hostId) {
		ClientAssociation clientAssociation = byHostId.remove(hostId);
		bySocketAddress.remove(clientAssociation.getSocketAddress());
		return clientAssociation;
	}

	public ClientAssociation deAssociate(InetSocketAddress address) {
		ClientAssociation clientAssociation = bySocketAddress.remove(address);
		byHostId.remove(clientAssociation.getHostId());
		return clientAssociation;
	}

	public boolean isAssociated(String hostId) {
		return byHostId.containsKey(hostId);
	}

	public boolean isAssociated(InetSocketAddress address) {
		return bySocketAddress.containsKey(address);
	}

	public Optional<ClientAssociation> findAssociation(String hostId) {
		return Optional.ofNullable(byHostId.get(hostId));
	}

	public Optional<ClientAssociation> findAssociation(InetSocketAddress address) {
		return Optional.ofNullable(bySocketAddress.get(address));
	}

	public Collection<ClientAssociation> getAssociations() {
		return Collections.unmodifiableCollection(byHostId.values());
	}
}
