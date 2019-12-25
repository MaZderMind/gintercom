package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import java.util.Collections;
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
import de.mazdermind.gintercom.shared.pipeline.support.PipelineException;

public class RtpTestClient {
	private final PortSet portSet;
	private Pipeline pipeline;

	private NoDataDetector noDataDetector;
	private PeakDetectorBin peakDetector;
	private PeakDetectorBin.ElementMessageHandler messageHandler;

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

	public RtpTestClient start() {
		if (pipeline != null) {
			throw new IllegalStateException("Cannot be started twice");
		}

		GstInvoker.invokeAndWait(() -> {
			String pipelineSpec = StringSubstitutor.replace("" +
					"audiotestsrc name=test_src freq=440 volume=0.0 is-live=true !\n" +
					"	${rawcaps} !\n" +
					"	audioconvert !\n" +
					"	${rawcaps_be} !\n" +
					"	rtpL16pay !\n" +
					"	${rtpcaps} !\n" +
					"	udpsink host=${matrix_host} port=${matrix_port}\n" +
					"\n" +
					"udpsrc port=${client_port} name=udp !\n" +
					"	${rtpcaps} !\n" +
					"	rtpjitterbuffer latency=50 drop-on-latency=true !\n" +
					"	identity sync=true !\n" +
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

			installNoDataDetector();
			installPeakDetector();
			installStateChangeLogger();

			pipeline.debugToDotFile(Bin.DebugGraphDetails.SHOW_ALL, "rtp-test-client");
			pipeline.play();
		});

		return this;
	}

	private void installStateChangeLogger() {
		PipelineStateChangeLogger stateChangeLogger = new PipelineStateChangeLogger();
		pipeline.getBus().connect((Bus.EOS) stateChangeLogger);
		pipeline.getBus().connect((Bus.ERROR) stateChangeLogger);
		pipeline.getBus().connect((Bus.STATE_CHANGED) stateChangeLogger);
	}

	private void installPeakDetector() {
		peakDetector = new PeakDetectorBin();
		pipeline.add(peakDetector);
		messageHandler = new PeakDetectorBin.ElementMessageHandler(peakDetector);
		pipeline.getBus().connect(messageHandler);
		pipeline.getElementByName("convert").link(peakDetector);
	}

	private void uninstallPeakDetector() {
		pipeline.getBus().disconnect(messageHandler);
		messageHandler = null;
	}

	private void installNoDataDetector() {
		noDataDetector = new NoDataDetector();
		pipeline.getElementByName("udp").getSrcPads().get(0)
			.addDataProbe(noDataDetector);
	}

	private void uninstallNoDataDetector() {
		pipeline.getElementByName("udp").getSrcPads().get(0).removeDataProbe(noDataDetector);
		noDataDetector = null;
	}

	public RtpTestClient stop() {
		GstInvoker.invokeAndWait(() -> {
			pipeline.stop();
			uninstallPeakDetector();
			uninstallNoDataDetector();
			pipeline = null;
		});

		return this;
	}

	private void ensureStarted() {
		if (pipeline == null) {
			this.start();
		}
	}

	/**
	 * Waits up to {@link PeakDetectorBin#DEFAULT_TIMEOUT} for Peaks in the Frequency Spectrum at the expected positions.
	 * Throws an AssertionError when the expected set of Peaks was not found before reaching the Timeout.
	 */
	public RtpTestClient awaitPeaks(List<Integer> peaks) {
		ensureStarted();

		peakDetector.awaitPeaks(peaks);
		return this;
	}

	/**
	 * Waits up to {@link PeakDetectorBin#DEFAULT_TIMEOUT} for Silence.
	 * Throws an AssertionError when no Silence was not found before reaching the Timeout.
	 */
	public RtpTestClient awaitSilence() {
		return awaitPeaks(Collections.emptyList());
	}

	/**
	 * Waits up to {@link NoDataDetector#DEFAULT_TIMEOUT} for a period of {@link NoDataDetector#DEFAULT_PERIOD} length, where no data
	 * arrives.
	 * Throws an AssertionError when no period without data of the required length was found before reaching the timeout.
	 */
	public RtpTestClient awaitNoMoreData() {
		noDataDetector.awaitNoMoreData();
		return this;
	}

	private static class PipelineStateChangeLogger implements Bus.EOS, Bus.STATE_CHANGED, Bus.ERROR {
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

		@Override
		public void errorMessage(GstObject source, int code, String message) {
			throw new PipelineException(message);
		}
	}
}
