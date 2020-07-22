package de.mazdermind.gintercom.debugclient.pipeline;

import java.net.InetAddress;
import java.util.List;

import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Element;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientsupport.pipeline.StandardClientPipeline;
import de.mazdermind.gintercom.clientsupport.pipeline.audiosupport.AudioSystem;
import de.mazdermind.gintercom.debugclient.pipeline.audiolevel.AudioLevelMessageListener;
import de.mazdermind.gintercom.gstreamersupport.GstBuilder;
import de.mazdermind.gintercom.gstreamersupport.GstConstants;
import de.mazdermind.gintercom.gstreamersupport.GstStaticCaps;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Primary
public class DebugClientPipeline extends StandardClientPipeline {
	private static final int AUDIO_LEVEL_INTERVAL = 40_000_000;
	private static final int MIX_PAD_MICROPHONE = 0;
	private static final int MIX_PAD_TONE = 1;

	private final AudioLevelMessageListener levelMessageListener;

	private boolean toneEnabled = false;
	private boolean microphoneEnabled = false;
	private boolean speakerEnabled = false;

	public DebugClientPipeline(
		AudioLevelMessageListener audioLevelMessageListener,
		List<AudioSystem> audioSystems
	) {
		super(audioSystems);
		levelMessageListener = audioLevelMessageListener;
	}

	@Override
	public void configurePipeline(InetAddress matrixAddress, int matrixToClientPort, int clientToMatrixPort) {
		super.configurePipeline(matrixAddress, matrixToClientPort, clientToMatrixPort);
		getPipeline().getBus().connect(levelMessageListener);

		configureTone(toneEnabled);
		configureMicrophone(microphoneEnabled);
		configureSpeaker(speakerEnabled);
	}

	@Override
	protected Element buildSourceElement() {
		// @formatter:off
		return GstBuilder.buildBin("debug-client-source")
			.addElement("audiomixer", "mix")
				.withProperty("start-time-selection", "first")
				.withProperty("output-buffer-duration", GstConstants.BUFFER_DURATION_NS)

			.addElement(super.buildSourceElement())
			.withCaps(GstStaticCaps.AUDIO)
				.linkExistingElement("mix")

			.addElement("audiotestsrc", "testsrc")
				.withProperty("is-live", true)
				.withProperty("volume", 0.25)
				.withProperty("samplesperbuffer", GstConstants.SAMPLES_PER_BUFFER)

			.withCaps(Caps.fromString("audio/x-raw,format=S16LE,rate=48000,channels=1"))
				.linkExistingElement("mix")

			.withGhostPad("mix", "src")
			.build();
		// @formatter:on
	}

	@Override
	protected Element buildSinkElement() {
		// @formatter:off
		return GstBuilder.buildBin("debug-client-sink")
			.addElement("level", "audiolevel")
				.withProperty("message", true)
				.withProperty("interval", AUDIO_LEVEL_INTERVAL)
			.linkElement("volume", "speaker_volume")
			.linkElement(super.buildSinkElement())

			.withGhostPad("audiolevel", "sink")
			.build();
		// @formatter:on
	}

	public void configureTone(boolean status) {
		log.info("{} Tone", status ? "Enabling" : "Disabling");
		toneEnabled = status;
		getPipeline().getElementByName("mix").getSinkPads().get(MIX_PAD_TONE).set("volume", status ? 1.0 : 0.0);
	}

	public void configureMicrophone(boolean status) {
		log.info("{} Microphone", status ? "Enabling" : "Disabling");
		microphoneEnabled = status;
		getPipeline().getElementByName("mix").getSinkPads().get(MIX_PAD_MICROPHONE).set("volume", status ? 1.0 : 0.0);
	}

	public void configureSpeaker(boolean status) {
		log.info("{} Speaker", status ? "Enabling" : "Disabling");
		speakerEnabled = status;
		getPipeline().getElementByName("speaker_volume").set("volume", status ? 1.0 : 0.0);
	}
}
