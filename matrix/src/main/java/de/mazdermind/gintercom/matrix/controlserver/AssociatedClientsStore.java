package de.mazdermind.gintercom.matrix.controlserver;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AssociatedClientsStore {
	private final Map<String, ClientAssociation> byClientId = new ConcurrentHashMap<>();
	private final Map<InetSocketAddress, ClientAssociation> bySocketAddress = new ConcurrentHashMap<>();

	public ClientAssociation associate(ClientAssociation clientAssociation) {
		byClientId.put(clientAssociation.getClientId(), clientAssociation);
		bySocketAddress.put(clientAssociation.getSocketAddress(), clientAssociation);
		return clientAssociation;
	}

	public ClientAssociation deAssociate(String clientId) {
		ClientAssociation clientAssociation = byClientId.remove(clientId);
		bySocketAddress.remove(clientAssociation.getSocketAddress());
		return clientAssociation;
	}

	public ClientAssociation deAssociate(InetSocketAddress address) {
		ClientAssociation clientAssociation = bySocketAddress.remove(address);
		byClientId.remove(clientAssociation.getClientId());
		return clientAssociation;
	}

	public boolean isAssociated(String clientId) {
		return byClientId.containsKey(clientId);
	}

	public boolean isAssociated(InetSocketAddress address) {
		return bySocketAddress.containsKey(address);
	}

	public Optional<ClientAssociation> findAssociation(String clientId) {
		return Optional.ofNullable(byClientId.get(clientId));
	}

	public Optional<ClientAssociation> findAssociation(InetSocketAddress address) {
		return Optional.ofNullable(bySocketAddress.get(address));
	}

	public Collection<ClientAssociation> getAssociations() {
		return Collections.unmodifiableCollection(byClientId.values());
	}
}
