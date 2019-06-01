package de.mazdermind.gintercom.matrix.pipeline;

import static de.mazdermind.gintercom.shared.pipeline.support.GstErrorCheck.expectTrue;

import java.net.InetAddress;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.GhostPad;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.pipeline.StaticCaps;
import de.mazdermind.gintercom.shared.pipeline.support.ElementFactory;

@Component
@Scope("prototype")
public class PanelTransmitPath {
	private static final Logger log = LoggerFactory.getLogger(PanelTransmitPath.class);

	private Bin bin;
	private Pipeline pipeline;
	private String panelId;
	private Element mixer;

	public void configure(Pipeline pipeline, String panelId, InetAddress host, int txPort) {
		log.info("Creating Transmit-Path for Panel {}", panelId);

		this.pipeline = pipeline;
		this.panelId = panelId;
		bin = new ElementFactory(pipeline).createAndAddBin(String.format("panel-%s-tx", panelId));
		ElementFactory factory = new ElementFactory(bin);

		Element silenceSrc = factory.createAndAddElement("audiotestsrc");
		silenceSrc.set("is-live", true);
		silenceSrc.set("freq", 220); // TODO wave=silence

		mixer = factory.createAndAddElement("audiomixer");
		Element.linkPadsFiltered(silenceSrc, "src", mixer, "sink_%u", StaticCaps.AUDIO);

		Element audioconvert = factory.createAndAddElement("audioconvert");
		mixer.link(audioconvert);

		Element payload = factory.createAndAddElement("rtpL16pay");
		Element.linkPadsFiltered(audioconvert, "src", payload, "sink", StaticCaps.AUDIO_BE);

		Element udpsink = factory.createAndAddElement("udpsink");
		udpsink.set("host", host.getHostAddress());
		udpsink.set("port", txPort);
		Element.linkPadsFiltered(payload, "src", udpsink, "sink", StaticCaps.RTP);

		bin.syncStateWithParent();
	}

	public void deconfigure() {
		log.info("Stopping Transmit-Path for Panel {}", panelId);
		bin.stop();

		log.info("Removing Transmit-Path for Panel {} from Pipeline", panelId);
		pipeline.remove(bin);
	}

	public Pad requestSinkPad() {
		Pad mixerPad = mixer.getRequestPad("sink_%u");
		GhostPad ghostPad = new GhostPad(null, mixerPad);
		expectTrue(ghostPad.setActive(true));
		expectTrue(bin.addPad(ghostPad));
		return ghostPad;
	}

	public void releaseSinkPad(GhostPad ghostPad) {
		Pad mixerPad = ghostPad.getTarget();
		expectTrue(bin.removePad(ghostPad));
		mixer.releaseRequestPad(mixerPad);
	}

}
