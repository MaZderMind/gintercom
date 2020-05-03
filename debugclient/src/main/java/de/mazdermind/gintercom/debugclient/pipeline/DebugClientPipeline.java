package de.mazdermind.gintercom.debugclient.pipeline;

import org.freedesktop.gstreamer.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import de.mazdermind.gintercom.clientsupport.controlserver.discovery.MatrixAddressDiscoveryServiceResult;
import de.mazdermind.gintercom.clientsupport.pipeline.DefaultPipeline;
import de.mazdermind.gintercom.debugclient.pipeline.audiolevel.AudioLevelMessageListener;
import de.mazdermind.gintercom.gstreamersupport.GstBuilder;
import de.mazdermind.gintercom.gstreamersupport.GstStaticCaps;

@Component
@Primary
public class DebugClientPipeline extends DefaultPipeline {
	private static final int AUDIO_LEVEL_INTERVAL = 40_000_000;
	private static final int MIX_PAD_MICROPHONE = 0;
	private static final int MIX_PAD_TONE = 1;

	private static final Logger log = LoggerFactory.getLogger(DebugClientPipeline.class);
	private final AudioLevelMessageListener levelMessageListener;

	public DebugClientPipeline(
		@Autowired AudioLevelMessageListener audioLevelMessageListener
	) {
		levelMessageListener = audioLevelMessageListener;
	}

	@Override
	public void configurePipeline(MatrixAddressDiscoveryServiceResult matrixAddress, ProvisioningInformation provisioningInformation) {
		super.configurePipeline(matrixAddress, provisioningInformation);
		getPipeline().getBus().connect(levelMessageListener);

		configureTone(false);
		configureMicrophone(false);
		configureSpeaker(false);
	}

	@Override
	protected Element buildSourceElement() {
		// @formatter:off
		return GstBuilder.buildBin("debug-client-source")
			.addElement("audiomixer", "mix")

			.addElement(super.buildSourceElement())
			.withCaps(GstStaticCaps.AUDIO)
				.linkExistingElement("mix")

			.addElement("audiotestsrc", "testsrc")
				.withProperty("is-live", true)
				.withProperty("volume", 0.25)
			.withCaps(GstStaticCaps.AUDIO)
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
		getPipeline().getElementByName("mix").getSinkPads().get(MIX_PAD_TONE).set("volume", status ? 1.0 : 0.0);
	}

	public void configureMicrophone(boolean status) {
		log.info("{} Microphone", status ? "Enabling" : "Disabling");
		getPipeline().getElementByName("mix").getSinkPads().get(MIX_PAD_MICROPHONE).set("volume", status ? 1.0 : 0.0);
	}

	public void configureSpeaker(boolean status) {
		log.info("{} Speaker", status ? "Enabling" : "Disabling");
		getPipeline().getElementByName("speaker_volume").set("volume", status ? 1.0 : 0.0);
	}
}
