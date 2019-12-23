package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import static de.mazdermind.gintercom.shared.pipeline.support.GstErrorCheck.expectAsyncOrSuccess;
import static de.mazdermind.gintercom.shared.pipeline.support.GstErrorCheck.expectTrue;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Scope;

import de.mazdermind.gintercom.shared.pipeline.StaticCaps;
import de.mazdermind.gintercom.shared.pipeline.support.ElementFactory;

@TestComponent
@Scope("prototype")
public class RtpTestClientTx {
	private static final int WAVE_SILENCE = 4;
	private static final int WAVE_SINE = 0;

	private static Logger log = LoggerFactory.getLogger(RtpTestClientTx.class);

	private Pipeline pipeline;
	private Element audiotestsrc;

	public void connect(Integer panelToMatrixPort) {
		pipeline = new Pipeline();
		ElementFactory factory = new ElementFactory(pipeline);

		audiotestsrc = factory.createAndAddElement("audiotestsrc");
		audiotestsrc.set("wave", WAVE_SILENCE);
		audiotestsrc.set("volume", 0.2);

		Element payloader = factory.createAndAddElement("rtpL16pay");
		expectTrue(audiotestsrc.linkFiltered(payloader, StaticCaps.AUDIO_BE));

		Element udpsink = factory.createAndAddElement("udpsink");
		udpsink.set("host", "127.0.0.1");
		udpsink.set("port", panelToMatrixPort);
		expectTrue(payloader.link(udpsink));

		log.info("Starting Test-RTP-Tx-Client on Port {}", panelToMatrixPort);
		expectAsyncOrSuccess(pipeline.play());
		pipeline.debugToDotFileWithTS(Bin.DebugGraphDetails.SHOW_ALL, "rtp-test-client-tx");
	}

	public void cleanup() {
		pipeline.stop();
	}

	public void transmitSine(float freq) {
		audiotestsrc.set("freq", (double) freq);
		audiotestsrc.set("wave", WAVE_SINE);
	}
}
