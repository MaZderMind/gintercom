package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import org.apache.commons.text.StringSubstitutor;
import org.freedesktop.gstreamer.Bin;
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

import de.mazdermind.gintercom.matrix.integration.tools.audioanalyzer.AudioAnalyser;
import de.mazdermind.gintercom.matrix.integration.tools.peakdetector.AppSinkSupport;
import de.mazdermind.gintercom.matrix.portpool.PortSet;
import de.mazdermind.gintercom.shared.pipeline.StaticCaps;
import de.mazdermind.gintercom.shared.pipeline.support.GstInvoker;
import de.mazdermind.gintercom.shared.pipeline.support.PipelineException;

public class RtpTestClient {
	private final PortSet portSet;
	private final String panelId;
	private final AudioAnalyser audioAnalyser;

	private Pipeline pipeline;

	public RtpTestClient(PortSet portSet) {
		this.portSet = portSet;
		this.panelId = null;
		this.audioAnalyser = new AudioAnalyser(48000, null);
	}

	public RtpTestClient(PortSet portSet, String panelId) {
		this.portSet = portSet;
		this.panelId = panelId;
		this.audioAnalyser = new AudioAnalyser(48000, panelId);
	}

	public RtpTestClient enableSine(double freq) {
		ensureStarted();

		GstInvoker.invokeAndWait(() -> {
			Element testSrc = pipeline.getElementByName("test_src");
			testSrc.set("freq", freq);
			testSrc.set("volume", 0.2);
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
					"  rtpjitterbuffer latency=50 drop-on-latency=true ! " +
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

			pipeline.debugToDotFileWithTS(Bin.DebugGraphDetails.SHOW_ALL, String.format("rtp-test-client-%s", panelId));
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

	private void installAudioAnalyzer() {
		AppSink appsink = (AppSink) pipeline.getElementByName("appsink");

		appsink.set("emit-signals", true);
		appsink.connect((AppSink.NEW_SAMPLE) appSink -> {
			long[] samples = AppSinkSupport.extractSampleValues(appSink.pullSample());
			audioAnalyser.appendSamples(samples);

			return FlowReturn.OK;
		});
	}

	public AudioAnalyser getAudioAnalyser() {
		ensureStarted();

		return audioAnalyser;
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


	public String getPanelId() {
		return panelId;
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
