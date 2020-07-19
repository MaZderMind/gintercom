package de.mazdermind.gintercom.clientsupport.pipeline;

import static de.mazdermind.gintercom.gstreamersupport.GstErrorCheck.expectSuccess;

import java.net.InetAddress;
import java.util.List;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;

import de.mazdermind.gintercom.clientsupport.pipeline.audiosupport.AudioSystem;
import de.mazdermind.gintercom.gstreamersupport.GstBuilder;
import de.mazdermind.gintercom.gstreamersupport.GstConstants;
import de.mazdermind.gintercom.gstreamersupport.GstDebugger;
import de.mazdermind.gintercom.gstreamersupport.GstStaticCaps;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class StandardClientPipeline implements ClientPipeline {
	static {
		Gst.init();
	}

	private final AudioSystem audioSystem;

	private Pipeline pipeline;
	private Bin txBin;
	private Bin rxBin;

	public StandardClientPipeline(List<AudioSystem> audioSystems) {
		audioSystem = audioSystems.stream()
			.filter(AudioSystem::available)
			.findFirst()
			.orElseThrow(() -> new RuntimeException("No AudioSystem available"));
		log.info("Using Audio-System {}", audioSystem.getClass().getSimpleName());
	}

	@Override
	public void configurePipeline(InetAddress matrixAddress, int matrixToPanelPort, int panelToMatrixPort) {
		log.info("Starting RTP/Audio Pipeline");
		pipeline = new Pipeline("ClientPipeline");

		txBin = buildTxBin(matrixAddress, panelToMatrixPort);
		rxBin = buildRxBin(matrixToPanelPort);

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
	public void destroyPipeline() {
		if (pipeline != null) {
			log.info("Stopping pipeline");
			pipeline.stop();

			pipeline = null;
			txBin = null;
			rxBin = null;
		}
	}

	protected Bin buildTxBin(InetAddress matrixAddress, int panelToMatrixPort) {
		// @formatter:off
		return GstBuilder.buildBin("client-tx")
			.addElement(buildSourceElement())
			.withCaps(Caps.fromString("audio/x-raw,format=S16LE,rate=48000,channels=1"))
			.linkElement("audioconvert")
			.withCaps(GstStaticCaps.AUDIO_BE)
			.linkElement("rtpL16pay")
				.withProperty("mtu", GstConstants.MTU)
			.withCaps(GstStaticCaps.RTP)
				.linkElement("udpsink", "client-udpsink")
					.withProperty("host", matrixAddress.getHostAddress())
					.withProperty("port", panelToMatrixPort)
					.withProperty("async", false)
					.withProperty("sync", false)
			.build();
		// @formatter:on
	}

	protected Bin buildRxBin(int matrixToPanelPort) {
		// @formatter:off
		return GstBuilder.buildBin("client-rx")
			.addElement("udpsrc", "client-udpsrc")
				.withProperty("port", matrixToPanelPort)
			.withCaps(GstStaticCaps.RTP)
			.linkElement("rtpjitterbuffer")
				.withProperty("latency", GstConstants.LATENCY_MS)
			.linkElement("rtpL16depay")
			.withCaps(GstStaticCaps.AUDIO_BE)
			.linkElement("audioconvert")
			.withCaps(Caps.fromString("audio/x-raw,format=S16LE,rate=48000,channels=1"))
			.linkElement(buildSinkElement())
			.build();
		// @formatter:on
	}

	protected void restartPipeline() {
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
		return audioSystem.buildSourceElement();
	}

	protected Element buildSinkElement() {
		return audioSystem.buildSinkElement();
	}
}
