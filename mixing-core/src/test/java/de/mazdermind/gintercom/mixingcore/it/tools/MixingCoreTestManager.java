package de.mazdermind.gintercom.mixingcore.it.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Panel;
import de.mazdermind.gintercom.mixingcore.it.portpool.PortPoolConfig;
import de.mazdermind.gintercom.mixingcore.it.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.it.portpool.PortSetPool;
import de.mazdermind.gintercom.mixingcore.it.tools.rtp.RtpTestClient;

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
	private final ArrayList<PanelAndClient> panels;
	private final ArrayList<Group> groups;

	private MixingCoreTestManager() {
		PortPoolConfig matrixToPanel = new PortPoolConfig().setStart(10000).setLimit(9999).setResetting(true);
		PortPoolConfig panelToMatrix = new PortPoolConfig().setStart(20000).setLimit(9999).setResetting(true);
		portSetPool = new PortSetPool(matrixToPanel, panelToMatrix);
		mixingCore = new MixingCore();

		panels = new ArrayList<>();
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
		panels.forEach(PanelAndClient::stopAndRemove);
		panels.clear();
		groups.forEach(group -> {
			if (mixingCore.hasGroup(group)) {
				mixingCore.removeGroup(group);
			}
		});
		groups.clear();
	}

	public Group addGroup(String name) {
		Group group = mixingCore.addGroup(name);
		groups.add(group);

		return group;
	}

	public PanelAndClient addPanel(String name) {
		PortSet ports = portSetPool.getNextPortSet();
		Panel panel = mixingCore.addPanel(name, MATRIX_HOST, ports.getPanelToMatrix(), ports.getMatrixToPanel());
		RtpTestClient client = new RtpTestClient(ports, name);
		PanelAndClient panelAndClient = new PanelAndClient(mixingCore, panel, client, ports);
		panels.add(panelAndClient);

		return panelAndClient;
	}

}
