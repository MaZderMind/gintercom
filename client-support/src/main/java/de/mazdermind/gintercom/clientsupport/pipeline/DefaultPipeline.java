package de.mazdermind.gintercom.clientsupport.pipeline;

import static de.mazdermind.gintercom.gstreamersupport.GstErrorCheck.expectSuccess;

import javax.annotation.PreDestroy;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.ElementFactory;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.gstreamersupport.GstBuilder;
import de.mazdermind.gintercom.gstreamersupport.GstConstants;
import de.mazdermind.gintercom.gstreamersupport.GstDebugger;
import de.mazdermind.gintercom.gstreamersupport.GstStaticCaps;
import lombok.extern.slf4j.Slf4j;

/**
 * Default RTP/Audio-Pipeline for Intercom-Clients, auto-detects Audio-Subsystem of the OS and
 * chooses the default audio sink/source.
 * <p>
 * If your Client needs more control over the audio in- or output, ie. add a Tone-Generator or special
 * Analyser or Filter in the In- or Output-Path, you can extend this Class and annotate it with
 * <code>@Component @Primary</code>
 */
@Slf4j
@Component
public class DefaultPipeline implements ClientPipeline {
	static {
		Gst.init();
	}

	private Pipeline pipeline;
	private Bin txBin;
	private Bin rxBin;

	@Override
	public void configurePipeline(MatrixAddressDiscoveryServiceResult matrixAddress, ProvisioningInformation provisioningInformation) {
		log.info("Starting RTP/Audio Pipeline");
		pipeline = new Pipeline("RtpTestClient");

		// @formatter:off
		txBin = GstBuilder.buildBin("client-tx")
			.addElement(buildSourceElement())
			.withCaps(GstStaticCaps.AUDIO)
			.linkElement("audioconvert")
			.withCaps(GstStaticCaps.AUDIO_BE)
			.linkElement("rtpL16pay")
				.withProperty("mtu", GstConstants.MTU)
			.withCaps(GstStaticCaps.RTP)
				.linkElement("udpsink", "client-udpsink")
					.withProperty("host", matrixAddress.getAddress().getHostAddress())
					.withProperty("port", provisioningInformation.getPanelToMatrixPort())
					.withProperty("async", false)
					.withProperty("sync", false)
			.build();
		// @formatter:on

		// @formatter:off
		rxBin = GstBuilder.buildBin("client-rx")
			.addElement("udpsrc", "client-udpsrc")
				.withProperty("port", provisioningInformation.getMatrixToPanelPort())
			.withCaps(GstStaticCaps.RTP)
			.linkElement("rtpjitterbuffer")
				.withProperty("latency", GstConstants.LATENCY_MS)
			.linkElement("rtpL16depay")
			.withCaps(GstStaticCaps.AUDIO_BE)
			.linkElement("audioconvert")
			.withCaps(GstStaticCaps.AUDIO)
			.linkElement(buildSinkElement())
			.build();
		// @formatter:on

		pipeline.add(rxBin);
		pipeline.add(txBin);

		pipeline.getBus().connect((Bus.WARNING) (source, code, message) -> {
			String msg = String.format("%s: %s", source.getName(), message);
			log.warn(msg);
		});
		pipeline.getBus().connect((Bus.ERROR) (source, code, message) -> {
			String msg = String.format("%s: %s", source.getName(), message);
			log.error(msg);
			restartPipeline();
		});
		pipeline.getBus().connect((Bus.EOS) source -> {
			String msg = String.format("%s: EOS", source.getName());
			log.error(msg);
			restartPipeline();
		});
	}

	@Override
	public void startPipeline() {
		log.info("Starting pipeline");
		expectSuccess(pipeline.play());
		GstDebugger.debugPipeline("client-pipeline", pipeline);
		log.info("Successfully started pipeline");
	}

	@Override
	@PreDestroy
	public void stopPipeline() {
		if (pipeline != null) {
			log.info("Stopping pipeline");
			pipeline.stop();

			pipeline = null;
			txBin = null;
			rxBin = null;
		}
	}

	private void restartPipeline() {
		log.warn("Caught an Uncorrectable Error in the Pipeline; Restarting");

		expectSuccess(pipeline.stop());
		expectSuccess(pipeline.play());
	}

	protected Pipeline getPipeline() {
		return pipeline;
	}

	protected Bin getTxBin() {
		return txBin;
	}

	protected Bin getRxBin() {
		return rxBin;
	}

	protected Element buildSourceElement() {
		Element src = ElementFactory.make("pulsesrc", "pulsesrc");
		src.set("client-name", "GIntercom Client (Source)");
		return src;
	}

	protected Element buildSinkElement() {
		Element sink = ElementFactory.make("pulsesink", "pulsesink");
		sink.set("client-name", "GIntercom Client (Sink)");
		sink.set("async", false);
		sink.set("sync", false);
		return sink;
	}
}
