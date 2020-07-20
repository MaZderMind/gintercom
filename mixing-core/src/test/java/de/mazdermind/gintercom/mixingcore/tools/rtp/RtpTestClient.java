package de.mazdermind.gintercom.mixingcore.tools.rtp;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.FlowReturn;
import org.freedesktop.gstreamer.GstException;
import org.freedesktop.gstreamer.GstObject;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.Sample;
import org.freedesktop.gstreamer.State;
import org.freedesktop.gstreamer.elements.AppSink;

import de.mazdermind.gintercom.gstreamersupport.GstBuilder;
import de.mazdermind.gintercom.gstreamersupport.GstConstants;
import de.mazdermind.gintercom.gstreamersupport.GstDebugger;
import de.mazdermind.gintercom.gstreamersupport.GstErrorCheck;
import de.mazdermind.gintercom.gstreamersupport.GstStaticCaps;
import de.mazdermind.gintercom.mixingcore.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.tools.audioanalyzer.AudioAnalyser;
import de.mazdermind.gintercom.mixingcore.tools.peakdetector.AppSinkSupport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RtpTestClient {
	private static final String TESTSRC_NAME = "client-testsrc";
	private static final String APPSINK_NAME = "client-appsink";

	private final PortSet portSet;
	private final String clientId;
	private final AudioAnalyser audioAnalyser;

	private AppSink.NEW_SAMPLE newSampleCallback;
	private PipelineStateChangeLogger stateChangeLogger;

	private Pipeline pipeline;
	private Bin txBin;
	private Bin rxBin;

	public RtpTestClient(PortSet portSet, String clientId) {
		this.portSet = portSet;
		this.clientId = clientId;
		this.audioAnalyser = new AudioAnalyser(48000, clientId);
	}

	public RtpTestClient enableSine(double freqency) {
		ensureStarted();

		Element testSrc = txBin.getElementByName(TESTSRC_NAME);
		testSrc.set("freq", freqency);
		testSrc.set("volume", 0.2);

		return this;
	}

	public RtpTestClient disableSine() {
		ensureStarted();

		Element testSrc = txBin.getElementByName(TESTSRC_NAME);
		testSrc.set("volume", 0.0);

		return this;
	}

	public RtpTestClient start() {
		if (pipeline != null) {
			throw new IllegalStateException("Cannot be started twice");
		}

		log.info("{}: Starting RtpTestClient for PortSet {}", clientId, portSet);
		pipeline = new Pipeline("RtpTestClient");

		// @formatter:off
		txBin = GstBuilder.buildBin("tx")
				.addElement("audiotestsrc", TESTSRC_NAME)
					.withProperty("is-live", true)
					.withProperty("volume", 0.0)
					.withProperty("samplesperbuffer", GstConstants.SAMPLES_PER_BUFFER)
				.withCaps(Caps.fromString("audio/x-raw,format=S16LE,rate=48000,channels=1"))
				.linkElement("audioconvert")
				.withCaps(GstStaticCaps.AUDIO_BE)
				.linkElement("rtpL16pay")
					.withProperty("mtu", GstConstants.MTU)
				.withCaps(GstStaticCaps.RTP)
				.linkElement("udpsink", "client-udpsink")
					.withProperty("host", "127.0.0.1")
					.withProperty("port", portSet.getClientToMatrix())
					.withProperty("async", false)
					.withProperty("sync", false)
				.build();
		// @formatter:on

		// @formatter:off
		rxBin = GstBuilder.buildBin("rx")
				.addElement("udpsrc", "client-udpsrc")
					.withProperty("port", portSet.getMatrixToClient())
				.withCaps(GstStaticCaps.RTP)
				.linkElement("rtpjitterbuffer")
					.withProperty("latency", GstConstants.LATENCY_MS)
				.linkElement("rtpL16depay")
				.withCaps(GstStaticCaps.AUDIO_BE)
				.linkElement("audioconvert")
				.withCaps(Caps.fromString("audio/x-raw,format=S16LE,rate=48000,channels=1"))
				.linkElement("appsink", APPSINK_NAME)
					.withProperty("async", false)
					.withProperty("sync", false)
				.build();
		// @formatter:on

		pipeline.add(rxBin);
		pipeline.add(txBin);

		installStateChangeLogger();
		installAudioAnalyzer();

		log.info("{}: starting pipeline", clientId);
		GstErrorCheck.expectSuccess(pipeline.play());
		GstDebugger.debugPipeline(String.format("rtp-test-client-%s", clientId), pipeline);
		log.info("{}: successfully started pipeline", clientId);

		return this;
	}

	private void installStateChangeLogger() {
		stateChangeLogger = new PipelineStateChangeLogger(clientId);
		pipeline.getBus().connect((Bus.EOS) stateChangeLogger);
		pipeline.getBus().connect((Bus.ERROR) stateChangeLogger);
		pipeline.getBus().connect((Bus.WARNING) stateChangeLogger);
		pipeline.getBus().connect((Bus.INFO) stateChangeLogger);
		pipeline.getBus().connect((Bus.STATE_CHANGED) stateChangeLogger);
	}

	private void uninstallStateChangeLogger() {
		pipeline.getBus().disconnect((Bus.EOS) stateChangeLogger);
		pipeline.getBus().disconnect((Bus.ERROR) stateChangeLogger);
		pipeline.getBus().disconnect((Bus.WARNING) stateChangeLogger);
		pipeline.getBus().disconnect((Bus.INFO) stateChangeLogger);
		pipeline.getBus().disconnect((Bus.STATE_CHANGED) stateChangeLogger);
	}

	private void installAudioAnalyzer() {
		AppSink appsink = (AppSink) rxBin.getElementByName(APPSINK_NAME);

		appsink.set("emit-signals", true);
		newSampleCallback = appSink -> {
			Sample sample = appSink.pullSample();
			if (sample == null) {
				log.warn("newSsample called without a sample being available");
				return FlowReturn.OK;
			}
			long[] samples = AppSinkSupport.extractSampleValues(sample);
			audioAnalyser.appendSamples(samples);

			return FlowReturn.OK;
		};
		appsink.connect(newSampleCallback);
	}

	private void uninstallAudioAnalyzer() {
		AppSink appsink = (AppSink) rxBin.getElementByName(APPSINK_NAME);
		appsink.disconnect(newSampleCallback);
	}

	public AudioAnalyser getAudioAnalyser() {
		ensureStarted();

		return audioAnalyser;
	}

	public void stop() {
		if (pipeline != null) {
			pipeline.stop();
			uninstallAudioAnalyzer();
			uninstallStateChangeLogger();
			pipeline = null;
		}
	}

	private void ensureStarted() {
		if (pipeline == null) {
			this.start();
		}
	}

	private static class PipelineStateChangeLogger implements Bus.EOS, Bus.STATE_CHANGED, Bus.INFO, Bus.WARNING, Bus.ERROR {
		private final String identifier;

		public PipelineStateChangeLogger(String identifier) {
			this.identifier = identifier;
		}

		@Override
		public void endOfStream(GstObject source) {
			log.error("{}: EOS received", identifier);
			throw new RuntimeException("EOS received");
		}

		@Override
		public void stateChanged(GstObject source, State old, State current, State pending) {
			if (source instanceof Pipeline) {
				log.info("{}: State changed from {} to {} pending {}", identifier, old, current, pending);
			} else {
				log.trace("{}: State changed from {} to {} pending {}", identifier, old, current, pending);
			}
		}

		@Override
		public void errorMessage(GstObject source, int code, String message) {
			throw new GstException(message);
		}

		@Override
		public void infoMessage(GstObject source, int code, String message) {
			log.info("{}: {}", identifier, message);
		}

		@Override
		public void warningMessage(GstObject source, int code, String message) {
			log.warn("{}: {}", identifier, message);
		}
	}
}
