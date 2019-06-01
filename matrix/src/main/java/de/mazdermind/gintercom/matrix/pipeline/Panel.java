package de.mazdermind.gintercom.matrix.pipeline;

import static de.mazdermind.gintercom.shared.pipeline.support.GstErrorCheck.expectNull;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.portpool.PortSet;

@Component
@Scope("prototype")
public class Panel {
	private static final Logger log = LoggerFactory.getLogger(Panel.class);

	private final PanelReceivePath receivePath;
	private final PanelTransmitPath transmitPath;

	private final Map<Group, Pad> transmitPathPads = new HashMap<>();
	private final Map<Group, Pad> receivePathPads = new HashMap<>();

	private String panelId;

	public Panel(
		@Autowired PanelReceivePath receivePath,
		@Autowired PanelTransmitPath transmitPath
	) {
		this.receivePath = receivePath;
		this.transmitPath = transmitPath;
	}

	public void configure(Pipeline pipeline, String panelId, PanelConfig panelConfig, PortSet portSet, InetAddress hostAddress) {
		log.info("Configuring Pipeline-Elements for Panel {}", panelId);
		this.panelId = panelId;

		receivePath.configure(pipeline, panelId, portSet.getPanelToMatrix());
		transmitPath.configure(pipeline, panelId, hostAddress, portSet.getMatrixToPanel());
	}

	public void deconfigure() {
		log.info("De-Configuring Pipeline-Elements for Panel {}", panelId);
		receivePath.deconfigure();
		transmitPath.deconfigure();
	}

	public void startTransmittingToGroup(Group group) {
		Pad sinkPad = transmitPath.requestSinkPad();
		expectNull(transmitPathPads.put(group, sinkPad));
		Pad srcPad = group.requestSrcPad();
		srcPad.link(sinkPad);
	}

	public void stopsTransmittingToGroup(Group group) {

	}

	public void startReceivingFromGroup(Group group) {

	}

	public void stopReceivingToGroup(Group group) {

	}
}
