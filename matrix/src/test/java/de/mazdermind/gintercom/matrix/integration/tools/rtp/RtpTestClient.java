package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import java.util.List;

import org.apache.commons.text.StringSubstitutor;
import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.GstObject;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import de.mazdermind.gintercom.matrix.portpool.PortSet;
import de.mazdermind.gintercom.shared.pipeline.StaticCaps;
import de.mazdermind.gintercom.shared.pipeline.support.GstInvoker;

public class RtpTestClient {
	private final PortSet portSet;
	private Pipeline pipeline;
	private PeakDetectorBin peakDetector;

	public RtpTestClient(PortSet portSet) {
		this.portSet = portSet;
	}

	public RtpTestClient enableSine(double freq) {
		ensureStarted();

		GstInvoker.invokeAndWait(() -> {
			Element testSrc = pipeline.getElementByName("test_src");
			testSrc.set("freq", freq);
			testSrc.set("volume", 0.8);
		});

		return this;
	}

	public RtpTestClient disableSine() {
		ensureStarted();

		GstInvoker.invokeAndWait(() -> {
			Element testSrc = pipeline.getElementByName("test_src");
			testSrc.set("volume", 0.0);
		});

		return this;
	}

	public RtpTestClient awaitPeaks(List<Integer> peaks) {
		ensureStarted();

		peakDetector.awaitPeaks(peaks);
		return this;
	}

	public RtpTestClient start() {
		if (pipeline != null) {
			throw new IllegalStateException("Cannot be started twice");
		}

		GstInvoker.invokeAndWait(() -> {
			String pipelineSpec = StringSubstitutor.replace("" +
					"audiotestsrc name=test_src freq=440 volume=0.0 !\n" +
					"	${rawcaps} !\n" +
					"	audioconvert !\n" +
					"	${rawcaps_be} !\n" +
					"	rtpL16pay !\n" +
					"	${rtpcaps} !\n" +
					"	udpsink host=${matrix_host} port=${matrix_port}\n" +
					"\n" +
					"udpsrc port=${client_port} !\n" +
					"	${rtpcaps} !\n" +
					"	rtpjitterbuffer latency=50 !\n" +
					"	rtpL16depay !\n" +
					"	${rawcaps_be} !\n" +
					"	audioconvert name=convert",
				ImmutableMap.<String, Object>builder()
					.put("rtpcaps", StaticCaps.RTP)
					.put("rawcaps", StaticCaps.AUDIO)
					.put("rawcaps_be", StaticCaps.AUDIO_BE)
					.put("matrix_host", "127.0.0.1")
					.put("matrix_port", portSet.getPanelToMatrix())
					.put("client_port", portSet.getMatrixToPanel())
					.build()
			);

			pipeline = (Pipeline) Gst.parseLaunch(pipelineSpec);

			peakDetector = new PeakDetectorBin();
			pipeline.add(peakDetector);
			pipeline.getBus().connect(new PeakDetectorBin.ElementMessageHandler(peakDetector));

			pipeline.getElementByName("convert").link(peakDetector);


			pipeline.debugToDotFile(Bin.DebugGraphDetails.SHOW_ALL, "rtp-test-client");
			PipelineStateChangeLogger stateChangeLogger = new PipelineStateChangeLogger();
			pipeline.getBus().connect((Bus.EOS) stateChangeLogger);
			pipeline.getBus().connect((Bus.STATE_CHANGED) stateChangeLogger);

			pipeline.play();
		});

		return this;
	}

	public RtpTestClient stop() {
		GstInvoker.invokeAndWait(() -> {
			pipeline.stop();
			pipeline = null;
		});

		return this;
	}

	private void ensureStarted() {
		if (pipeline == null) {
			this.start();
		}
	}

	private static class PipelineStateChangeLogger implements Bus.EOS, Bus.STATE_CHANGED {
		private static final Logger log = LoggerFactory.getLogger(PipelineStateChangeLogger.class);

		@Override
		public void endOfStream(GstObject source) {
			log.error("EOS received");
			throw new RuntimeException("EOS received");
		}

		@Override
		public void stateChanged(GstObject source, State old, State current, State pending) {
			if (source instanceof Pipeline) {
				log.info("State changed from {} to {} pending {}", old, current, pending);
			} else {
				log.trace("State changed from {} to {} pending {}", old, current, pending);
			}
		}
	}
}
