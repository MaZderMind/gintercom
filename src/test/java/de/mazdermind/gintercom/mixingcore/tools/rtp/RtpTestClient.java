package de.mazdermind.gintercom.mixingcore.tools.rtp;

import org.apache.commons.text.StringSubstitutor;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.FlowReturn;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.GstObject;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.State;
import org.freedesktop.gstreamer.elements.AppSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import de.mazdermind.gintercom.mixingcore.StaticCaps;
import de.mazdermind.gintercom.mixingcore.portpool.PortSet;
import de.mazdermind.gintercom.mixingcore.support.GstDebugger;
import de.mazdermind.gintercom.mixingcore.support.PipelineException;
import de.mazdermind.gintercom.mixingcore.tools.audioanalyzer.AudioAnalyser;
import de.mazdermind.gintercom.mixingcore.tools.peakdetector.AppSinkSupport;

public class RtpTestClient {
	private static final Logger log = LoggerFactory.getLogger(RtpTestClient.class);

	private final PortSet portSet;
	private final String panelId;
	private final AudioAnalyser audioAnalyser;

	private Pipeline pipeline;
	private AppSink.NEW_SAMPLE newSampleCallback;
	private PipelineStateChangeLogger stateChangeLogger;

	public RtpTestClient(PortSet portSet, String panelId) {
		this.portSet = portSet;
		this.panelId = panelId;
		this.audioAnalyser = new AudioAnalyser(48000, panelId);
	}

	public RtpTestClient enableSine(double freq) {
		ensureStarted();

		Element testSrc = pipeline.getElementByName("test_src");
		testSrc.set("freq", freq);
		testSrc.set("volume", 0.2);

		return this;
	}

	public RtpTestClient disableSine() {
		ensureStarted();

		Element testSrc = pipeline.getElementByName("test_src");
		testSrc.set("volume", 0.0);

		return this;
	}

	public RtpTestClient start() {
		if (pipeline != null) {
			throw new IllegalStateException("Cannot be started twice");
		}

		log.info("{}: Starting RtpTestClient for PortSet {}", panelId, portSet);

		String pipelineSpec = StringSubstitutor.replace("" +
						"audiotestsrc name=test_src freq=440 volume=0.0 is-live=true ! " +
						"  ${rawcaps} ! " +
						"  audioconvert ! " +
						"  ${rawcaps_be} ! " +
						"  rtpL16pay ! " +
						"  ${rtpcaps} ! " +
						"  udpsink host=${matrix_host} port=${matrix_port} " +
						"" +
						"udpsrc port=${client_port} name=udp ! " +
						"  ${rtpcaps} ! " +
						"  rtpjitterbuffer latency=50 ! " +
						"  identity sync=true ! " +
						"  rtpL16depay ! " +
						"  ${rawcaps_be} ! " +
						"  audioconvert ! " +
						"  ${rawcaps} ! " +
						"  appsink name=appsink sync=false",
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

		installStateChangeLogger();
		installAudioAnalyzer();

		GstDebugger.debugPipeline(String.format("rtp-test-client-%s", panelId), pipeline);
		pipeline.play();

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
		AppSink appsink = (AppSink) pipeline.getElementByName("appsink");

		appsink.set("emit-signals", true);
		newSampleCallback = appSink -> {
			long[] samples = AppSinkSupport.extractSampleValues(appSink.pullSample());
			audioAnalyser.appendSamples(samples);

			return FlowReturn.OK;
		};
		appsink.connect(newSampleCallback);
	}

	private void uninstallAudioAnalyzer() {
		AppSink appsink = (AppSink) pipeline.getElementByName("appsink");
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


	public String getPanelId() {
		return panelId;
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
