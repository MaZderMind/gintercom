package de.mazdermind.gintercom.mixingcore.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import de.mazdermind.gintercom.mixingcore.Client;
import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.portpool.PortPoolConfig;
import de.mazdermind.gintercom.mixingcore.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.portpool.PortSetPool;
import de.mazdermind.gintercom.mixingcore.tools.rtp.RtpTestClient;

public class MixingCoreTestManager {
	public static final InetAddress MATRIX_HOST;
	private static MixingCoreTestManager instance;

	static {
		try {
			MATRIX_HOST = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	private final PortSetPool portSetPool;
	private final MixingCore mixingCore;
	private final ArrayList<ClientInfo> clients;
	private final ArrayList<Group> groups;

	private MixingCoreTestManager() {
		PortPoolConfig matrixToClient = new PortPoolConfig().setStart(10000).setLimit(9999).setResetting(true);
		PortPoolConfig clientToMatrix = new PortPoolConfig().setStart(20000).setLimit(9999).setResetting(true);
		portSetPool = new PortSetPool(matrixToClient, clientToMatrix);
		mixingCore = new MixingCore();

		clients = new ArrayList<>();
		groups = new ArrayList<>();
	}

	public static MixingCoreTestManager getInstance() {
		if (instance == null) {
			instance = new MixingCoreTestManager();
		}

		return instance;
	}

	public PortSetPool getPortSetPool() {
		return portSetPool;
	}

	public MixingCore getMixingCore() {
		return mixingCore;
	}

	public void cleanup() {
		clients.forEach(ClientInfo::stopAndRemove);
		clients.clear();
		groups.forEach(group -> {
			if (mixingCore.hasGroup(group)) {
				mixingCore.removeGroup(group);
			}
		});
		groups.clear();
	}

	public Group addGroup(String id) {
		Group group = mixingCore.addGroup(id);
		groups.add(group);

		return group;
	}

	public ClientInfo addClient(String id) {
		PortSet ports = portSetPool.getNextPortSet();
		Client client = mixingCore.addClient(id, MATRIX_HOST, ports.getClientToMatrix(), ports.getMatrixToClient());
		RtpTestClient rtpTestClient = new RtpTestClient(ports, id);
		ClientInfo clientInfo = new ClientInfo(mixingCore, client, rtpTestClient, ports);
		clients.add(clientInfo);

		return clientInfo;
	}
}
