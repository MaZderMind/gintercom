package de.mazdermind.gintercom.mixingcore;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.State;

import de.mazdermind.gintercom.mixingcore.exception.InvalidMixingCoreOperationException;
import de.mazdermind.gintercom.mixingcore.exception.MixingCoreException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MixingCore {
	static {
		Gst.init();
	}

	private final Pipeline pipeline;

	private final Map<String, Client> clients = new HashMap<>();
	private final Map<String, Group> groups = new HashMap<>();

	public MixingCore() {
		pipeline = new Pipeline("matrix");
		pipeline.play();

		pipeline.getBus().connect((Bus.WARNING) (source, code, message) -> {
			String msg = String.format("%s: %s", source.getName(), message);
			log.warn(msg);
		});
		pipeline.getBus().connect((Bus.ERROR) (source, code, message) -> {
			String msg = String.format("%s: %s", source.getName(), message);
			log.error(msg);
			throw new MixingCoreException(msg);
		});
		pipeline.getBus().connect((Bus.EOS) source -> {
			String msg = String.format("%s: EOS", source.getName());
			log.error(msg);
			throw new MixingCoreException(msg);
		});
	}


	public Group addGroup(String id) {
		if (groups.containsKey(id)) {
			throw new InvalidMixingCoreOperationException(String.format("Group '%s' is already registered", id));
		}

		Group group = new Group(pipeline, id);
		groups.put(id, group);
		return group;
	}

	public Client addClient(String id, InetAddress clientHost, int clientToMatrixPort, int matrixToClientPort) {
		if (clients.containsKey(id)) {
			throw new InvalidMixingCoreOperationException(String.format("Client '%s' is already registered", id));
		}

		Client client = new Client(pipeline, id, clientHost, clientToMatrixPort, matrixToClientPort);
		clients.put(id, client);
		return client;
	}

	public Group getGroupById(String id) {
		return groups.get(id);
	}

	public Client getClientById(String id) {
		return clients.get(id);
	}

	public boolean hasGroup(@Nonnull Group group) {
		return group.equals(groups.get(group.getId()));
	}

	public boolean hasClient(@Nonnull Client client) {
		return client.equals(clients.get(client.getId()));
	}

	public Collection<Group> getGroups() {
		return Collections.unmodifiableCollection(groups.values());
	}

	public Collection<Client> getClients() {
		return Collections.unmodifiableCollection(clients.values());
	}

	public void removeGroup(@Nonnull Group group) {
		if (groups.remove(group.getId()) == null) {
			throw new InvalidMixingCoreOperationException(String.format("Group %s not registered", group.getId()));
		}

		group.remove();
	}

	public void removeClient(@Nonnull Client client) {
		if (clients.remove(client.getId()) == null) {
			throw new InvalidMixingCoreOperationException(String.format("Client %s not registered", client.getId()));
		}

		client.remove();
	}

	public void clear() {
		log.info("Removing all Clients and Groups");
		List<Client> clientsToRemove = new ArrayList<>(this.clients.values());
		clientsToRemove.forEach(this::removeClient);

		List<Group> groupsToRemove = new ArrayList<>(this.groups.values());
		groupsToRemove.forEach(this::removeGroup);
	}

	public void shutdown() {
		clear();

		log.info("Shutting down Pipeline");
		pipeline.setState(State.NULL);
	}

	public boolean isRunning() {
		return pipeline.getState() == State.PLAYING;
	}
}
