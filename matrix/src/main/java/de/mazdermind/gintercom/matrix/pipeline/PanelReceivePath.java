package de.mazdermind.gintercom.matrix.pipeline;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.matrix.pipeline.support.ElementFactory;

@Component
@Scope("prototype")
public class PanelReceivePath {
	private static Logger log = LoggerFactory.getLogger(PanelReceivePath.class);

	private final Config config;

	public PanelReceivePath(
		@Autowired Config config
	) {
		this.config = config;
	}

	public void configure(Pipeline pipeline, String panelId, PanelConfig panelConfig) {
		log.info("Creating Receive-Path for Panel {}", panelId);
		Bin bin = new ElementFactory(pipeline).createAndAddBin(String.format("bin-panel-rx-%s", panelId));

		ElementFactory factory = new ElementFactory(bin);

		// udpsrc port=20003 ! {rtpcaps} ! rtpjitterbuffer latency=50 ! rtpL16depay ! {rawcaps_be} ! audioconvert ! {rawcaps} ! tee name=src_3
		Element udpsrc = factory.createAndAddElement("udpsrc");
		udpsrc.set("port", panelConfig.getFixedIp().getClientPort());  // FIXME is actually optional in config

		Element jitterbuffer = factory.createAndAddElement("rtpjitterbuffer");
		jitterbuffer.set("latency", config.getMatrixConfig().getRtp().getJitterbuffer());
		Element.linkPadsFiltered(udpsrc, "src", jitterbuffer, "sink", StaticCaps.RTP);

		Element depayload = factory.createAndAddElement("rtpL16depay");
		jitterbuffer.link(depayload);

		Element audioconvert = factory.createAndAddElement("audioconvert");
		Element.linkPadsFiltered(depayload, "src", audioconvert, "sink", StaticCaps.AUDIO_BE);

		Element tee = factory.createAndAddElement("tee", String.format("panel-rx-%s", panelId));
		Element.linkPadsFiltered(audioconvert, "src", tee, "sink", StaticCaps.AUDIO);
	}
}
