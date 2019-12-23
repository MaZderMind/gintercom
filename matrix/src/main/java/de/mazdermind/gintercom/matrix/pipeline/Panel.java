package de.mazdermind.gintercom.matrix.pipeline;

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

	private final Map<Group, Pad> txPads = new HashMap<>();
	private final Map<Group, Pad> rxPads = new HashMap<>();

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
		log.info("Releasing Rx-Pads");
		txPads.forEach(Group::releaseSinkPad);
		txPads.clear();

		log.info("Releasing Tx-Pads");
		rxPads.forEach(Group::releaseSrcPad);
		rxPads.clear();

		log.info("De-Configuring Pipeline-Elements for Panel {}", panelId);
		receivePath.deconfigure();
		transmitPath.deconfigure();
	}

	public void startTransmittingToGroup(Group group) {
		Pad srcPad = receivePath.requestSrcPad();
		Pad sinkPad = group.requestSinkPad();
		txPads.put(group, sinkPad);
		srcPad.link(sinkPad);
	}

	public void stopTransmittingToGroup(Group group) {
		// FIXME implement me
	}

	public void startReceivingFromGroup(Group group) {
		Pad sinkPad = transmitPath.requestSinkPad();
		Pad srcPad = group.requestSrcPad();
		rxPads.put(group, srcPad);
		srcPad.link(sinkPad);
	}

	public void stopReceivingToGroup(Group group) {
		// FIXME implement me
	}
}
