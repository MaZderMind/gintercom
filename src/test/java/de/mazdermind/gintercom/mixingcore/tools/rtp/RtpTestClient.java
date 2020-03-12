package de.mazdermind.gintercom.mixingcore.tools.rtp;

import static de.mazdermind.gintercom.mixingcore.support.GstErrorCheck.expectAsyncOrSuccess;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.FlowReturn;
import org.freedesktop.gstreamer.GstObject;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.State;
import org.freedesktop.gstreamer.elements.AppSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mazdermind.gintercom.mixingcore.StaticCaps;
import de.mazdermind.gintercom.mixingcore.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.support.GstBuilder;
import de.mazdermind.gintercom.mixingcore.support.GstDebugger;
import de.mazdermind.gintercom.mixingcore.support.PipelineException;
import de.mazdermind.gintercom.mixingcore.tools.audioanalyzer.AudioAnalyser;
import de.mazdermind.gintercom.mixingcore.tools.peakdetector.AppSinkSupport;

public class RtpTestClient {
	private static final Logger log = LoggerFactory.getLogger(RtpTestClient.class);

	private final PortSet portSet;
	private final String panelId;
	private final AudioAnalyser audioAnalyser;

	private AppSink.NEW_SAMPLE newSampleCallback;
	private PipelineStateChangeLogger stateChangeLogger;

	private Pipeline pipeline;
	private Bin txBin;
	private Bin rxBin;

	public RtpTestClient(PortSet portSet, String panelId) {
		this.portSet = portSet;
		this.panelId = panelId;
		this.audioAnalyser = new AudioAnalyser(48000, panelId);
	}

	public RtpTestClient enableSine(double freq) {
		ensureStarted();

		Element testSrc = txBin.getElementByName("test_src");
		testSrc.set("freq", freq);
		testSrc.set("volume", 0.2);

		return this;
	}

	public RtpTestClient disableSine() {
		ensureStarted();

		Element testSrc = txBin.getElementByName("test_src");
		testSrc.set("volume", 0.0);

		return this;
	}

	public RtpTestClient start() {
		if (pipeline != null) {
			throw new IllegalStateException("Cannot be started twice");
		}

		log.info("{}: Starting RtpTestClient for PortSet {}", panelId, portSet);
		pipeline = new Pipeline("RtpTestClient");

		// @formatter:off
		txBin = GstBuilder.buildBin("tx")
				.addElement("audiotestsrc", "test_src")
					.withProperty("is-live", true)
					.withProperty("volume", 0.0)
				.withCaps(StaticCaps.AUDIO)
				.linkElement("audioconvert")
				.withCaps(StaticCaps.AUDIO_BE)
				.linkElement("rtpL16pay")
				.withCaps(StaticCaps.RTP)
				.linkElement("udpsink")
					.withProperty("host", "127.0.0.1")
					.withProperty("port", portSet.getPanelToMatrix())
					//.withProperty("sync", false)
					.withProperty("async", false)
				.build();
		// @formatter:on

		// @formatter:off
		rxBin = GstBuilder.buildBin("rx")
				.addElement("udpsrc")
					.withProperty("port", portSet.getMatrixToPanel())
				.withCaps(StaticCaps.RTP)
				.linkElement("rtpjitterbuffer")
					.withProperty("latency", 100)
				.linkElement("rtpL16depay")
				.withCaps(StaticCaps.AUDIO_BE)
				.linkElement("audioconvert")
				.withCaps(StaticCaps.AUDIO)
				.linkElement("appsink", "appsink")
					.withProperty("sync", false)
				.build();
		// @formatter:on

		pipeline.add(rxBin);
		pipeline.add(txBin);

		installStateChangeLogger();
		installAudioAnalyzer();

		expectAsyncOrSuccess(pipeline.play());
		GstDebugger.debugPipeline(String.format("rtp-test-client-%s", panelId), pipeline);

		return this;
	}

	private void installStateChangeLogger() {
		stateChangeLogger = new PipelineStateChangeLogger(panelId);
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
		AppSink appsink = (AppSink) rxBin.getElementByName("appsink");

		appsink.set("emit-signals", true);
		newSampleCallback = appSink -> {
			long[] samples = AppSinkSupport.extractSampleValues(appSink.pullSample());
			audioAnalyser.appendSamples(samples);

			return FlowReturn.OK;
		};
		appsink.connect(newSampleCallback);
	}

	private void uninstallAudioAnalyzer() {
		AppSink appsink = (AppSink) rxBin.getElementByName("appsink");
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
			throw new PipelineException(message);
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
