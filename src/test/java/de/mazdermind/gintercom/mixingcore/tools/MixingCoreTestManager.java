package de.mazdermind.gintercom.mixingcore.tools;

import java.util.ArrayList;

import org.freedesktop.gstreamer.Gst;

import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Panel;
import de.mazdermind.gintercom.mixingcore.portpool.PortPoolConfig;
import de.mazdermind.gintercom.mixingcore.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.portpool.PortSetPool;
import de.mazdermind.gintercom.mixingcore.tools.rtp.RtpTestClient;

public class MixingCoreTestManager {
	private static final String LOCALHOST = "127.0.0.1";

	private static MixingCoreTestManager instance;

	private final PortSetPool portSetPool;
	private final MixingCore mixingCore;

	private final ArrayList<PanelAndClient> panels;
	private final ArrayList<Group> groups;

	private MixingCoreTestManager() {
		Gst.init();

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

	public void cleanup() {
		panels.forEach(PanelAndClient::stopAndRemove);
		panels.clear();
		groups.forEach(Group::remove);
		groups.clear();
	}

	public Group addGroup(String name) {
		Group group = mixingCore.addGroup(name);
		groups.add(group);

		return group;
	}

	public PanelAndClient addPanel(String name) {
		PortSet ports = portSetPool.getNextPortSet();
		Panel panel = mixingCore.addPanel(name, LOCALHOST, ports.getPanelToMatrix(), ports.getMatrixToPanel());
		RtpTestClient client = new RtpTestClient(ports, name);
		PanelAndClient panelAndClient = new PanelAndClient(panel, client);
		panels.add(panelAndClient);

		return panelAndClient;
	}

}
