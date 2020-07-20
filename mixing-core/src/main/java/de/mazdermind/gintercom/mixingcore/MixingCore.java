package de.mazdermind.gintercom.mixingcore;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


	public Group addGroup(String name) {
		if (groups.containsKey(name)) {
			throw new InvalidMixingCoreOperationException(String.format("Group %s already registered", name));
		}

		Group group = new Group(pipeline, name);
		groups.put(name, group);
		return group;
	}

	public Client addClient(String name, InetAddress clientHost, int clientToMatrixPort, int matrixToClientPort) {
		if (clients.containsKey(name)) {
			throw new InvalidMixingCoreOperationException(String.format("Client %s already registered", name));
		}

		Client client = new Client(pipeline, name, clientHost, clientToMatrixPort, matrixToClientPort);
		clients.put(name, client);
		return client;
	}

	public Group getGroupByName(String name) {
		return groups.get(name);
	}

	public Client getClientByName(String name) {
		return clients.get(name);
	}

	public boolean hasGroup(@Nonnull Group group) {
		return group.equals(groups.get(group.getName()));
	}

	public boolean hasClient(@Nonnull Client client) {
		return client.equals(clients.get(client.getName()));
	}

	public Set<String> getGroupNames() {
		return groups.keySet();
	}

	public Set<String> getClientNames() {
		return clients.keySet();
	}

	public void removeGroup(@Nonnull Group group) {
		if (groups.remove(group.getName()) == null) {
			throw new InvalidMixingCoreOperationException(String.format("Group %s not registered", group.getName()));
		}

		group.remove();
	}

	public void removeClient(@Nonnull Client client) {
		if (clients.remove(client.getName()) == null) {
			throw new InvalidMixingCoreOperationException(String.format("Client %s not registered", client.getName()));
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
