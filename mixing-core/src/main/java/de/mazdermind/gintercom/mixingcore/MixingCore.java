package de.mazdermind.gintercom.mixingcore;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mazdermind.gintercom.mixingcore.exception.InvalidOperationException;
import de.mazdermind.gintercom.mixingcore.support.GstException;

public class MixingCore {
	private static final Logger log = LoggerFactory.getLogger(MixingCore.class);

	static {
		Gst.init();
	}

	private final Pipeline pipeline;

	private final Map<String, Panel> panels = new HashMap<>();
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
			throw new GstException(msg);
		});
		pipeline.getBus().connect((Bus.EOS) source -> {
			String msg = String.format("%s: EOS", source.getName());
			log.error(msg);
			throw new GstException(msg);
		});
	}


	public Group addGroup(String name) {
		if (groups.containsKey(name)) {
			throw new InvalidOperationException(String.format("Group %s already registered", name));
		}

		Group group = new Group(pipeline, name);
		groups.put(name, group);
		return group;
	}

	public Panel addPanel(String name, InetAddress panelHost, int panelToMatrixPort, int matrixToPanelPort) {
		if (panels.containsKey(name)) {
			throw new InvalidOperationException(String.format("Panel %s already registered", name));
		}

		Panel panel = new Panel(pipeline, name, panelHost, panelToMatrixPort, matrixToPanelPort);
		panels.put(name, panel);
		return panel;
	}

	public Group getGroupByName(String name) {
		return groups.get(name);
	}

	public Panel getPanelByName(String name) {
		return panels.get(name);
	}

	public boolean hasGroup(@Nonnull Group group) {
		return group.equals(groups.get(group.getName()));
	}

	public boolean hasPanel(@Nonnull Panel panel) {
		return panel.equals(panels.get(panel.getName()));
	}

	public Set<String> getGroupNames() {
		return groups.keySet();
	}

	public Set<String> getPanelNames() {
		return panels.keySet();
	}

	public void removeGroup(@Nonnull Group group) {
		if (groups.remove(group.getName()) == null) {
			throw new InvalidOperationException(String.format("Group %s not registered", group.getName()));
		}

		group.remove();
	}

	public void removePanel(@Nonnull Panel panel) {
		if (panels.remove(panel.getName()) == null) {
			throw new InvalidOperationException(String.format("Panel %s not registered", panel.getName()));
		}

		panel.remove();
	}
}
