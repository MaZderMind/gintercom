package de.mazdermind.gintercom.matrix.pipeline;

import java.net.InetAddress;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
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
	private static Logger log = LoggerFactory.getLogger(PanelTransmitPath.class);

	private Bin bin;
	private Pipeline pipeline;
	private String panelId;

	public void configure(Pipeline pipeline, String panelId, InetAddress host, int txPort) {
		log.info("Creating Transmit-Path for Panel {}", panelId);

		this.pipeline = pipeline;
		this.panelId = panelId;
		this.bin = new ElementFactory(pipeline).createAndAddBin(String.format("bin-panel-tx-%s", panelId));

		ElementFactory factory = new ElementFactory(bin);

		// audiomixer name=sink_3 ! {rawcaps} ! audioconvert ! {rawcaps_be} ! rtpL16pay ! {rtpcaps} ! udpsink host=127.0.0.1 port=10003
		Element audiomixer = factory.createAndAddElement("audiomixer", String.format("panel-tx-%s", panelId));

		Element audioconvert = factory.createAndAddElement("audioconvert");
		Element.linkPadsFiltered(audiomixer, "src", audioconvert, "sink", StaticCaps.AUDIO);

		Element payload = factory.createAndAddElement("rtpL16pay");
		Element.linkPadsFiltered(audioconvert, "src", payload, "sink", StaticCaps.AUDIO_BE);

		Element udpsink = factory.createAndAddElement("udpsink");
		udpsink.set("host", host.getHostAddress());
		udpsink.set("port", txPort);
		Element.linkPadsFiltered(payload, "src", udpsink, "sink", StaticCaps.RTP);

		log.info("Starting Receive-Path for Panel {}", panelId);
		bin.play();
	}

	public void deconfigure() {
		log.info("Stopping Transmit-Path for Panel {}", panelId);
		bin.stop();

		log.info("Removing Transmit-Path for Panel {} from Pipeline", panelId);
		pipeline.remove(bin);
	}
}
