package de.mazdermind.gintercom.matrix.pipeline;

import static de.mazdermind.gintercom.shared.pipeline.support.GstErrorCheck.expectTrue;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.GhostPad;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.shared.pipeline.StaticCaps;
import de.mazdermind.gintercom.shared.pipeline.support.ElementFactory;

@Component
@Scope("prototype")
public class PanelReceivePath {
	private static final Logger log = LoggerFactory.getLogger(PanelReceivePath.class);

	private final Config config;

	private Bin bin;
	private Pipeline pipeline;
	private String panelId;
	private Element tee;

	public PanelReceivePath(
		@Autowired Config config
	) {
		this.config = config;
	}

	public void configure(Pipeline pipeline, String panelId, int rxPort) {
		log.info("Creating Receive-Path for Panel {}", panelId);

		this.pipeline = pipeline;
		this.panelId = panelId;
		bin = new ElementFactory(pipeline).createAndAddBin(String.format("panel-%s-rx", panelId));
		ElementFactory factory = new ElementFactory(bin);

		Element udpsrc = factory.createAndAddElement("udpsrc");
		udpsrc.set("caps", StaticCaps.RTP);
		udpsrc.set("port", rxPort);

		Element jitterbuffer = factory.createAndAddElement("rtpjitterbuffer");
		jitterbuffer.set("latency", config.getMatrixConfig().getRtp().getJitterbuffer());
		jitterbuffer.set("drop-on-latency", true);
		udpsrc.link(jitterbuffer);

		Element depayloader = factory.createAndAddElement("rtpL16depay");
		jitterbuffer.link(depayloader);

		Element audioconvert = factory.createAndAddElement("audioconvert");
		depayloader.link(audioconvert);

		tee = factory.createAndAddElement("tee");
		tee.set("allow-not-linked", true);
		audioconvert.linkFiltered(tee, StaticCaps.AUDIO);

		bin.syncStateWithParent();
	}

	public void deconfigure() {
		log.info("Stopping Receive-Path for Panel {}", panelId);
		bin.stop();

		log.info("Removing Receive-Path for Panel {} from Pipeline", panelId);
		pipeline.remove(bin);
	}

	public Pad requestSrcPad() {
		Pad teePad = tee.getRequestPad("src_%u");
		GhostPad ghostPad = new GhostPad(null, teePad);
		expectTrue(ghostPad.setActive(true));
		expectTrue(bin.addPad(ghostPad));
		return ghostPad;
	}

	public void releaseSrcPad(GhostPad ghostPad) {
		Pad teePad = ghostPad.getTarget();
		expectTrue(bin.removePad(ghostPad));
		tee.releaseRequestPad(teePad);
	}
}
