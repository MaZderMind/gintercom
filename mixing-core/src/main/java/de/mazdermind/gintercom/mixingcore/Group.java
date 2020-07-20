package de.mazdermind.gintercom.mixingcore;

import static de.mazdermind.gintercom.gstreamersupport.GstDebugger.debugPipeline;
import static de.mazdermind.gintercom.gstreamersupport.GstErrorCheck.expectSuccess;

import java.util.HashSet;
import java.util.Set;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.GhostPad;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.Pipeline;

import de.mazdermind.gintercom.gstreamersupport.GstBuilder;
import de.mazdermind.gintercom.gstreamersupport.GstConstants;
import de.mazdermind.gintercom.gstreamersupport.GstPadBlock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Group {
	private final String id;

	private final Pipeline pipeline;
	private final Bin bin;

	private final Element tee;
	private final Element mixer;

	private final Set<Client> inClients = new HashSet<>();
	private final Set<Client> outClients = new HashSet<>();

	Group(Pipeline pipeline, String id) {
		log.info("Creating Group {}", id);

		this.id = id;
		this.pipeline = pipeline;

		String teeName = String.format("group-%s-out", id);
		String mixerName = String.format("group-%s-in", id);

		// @formatter:off
		bin = GstBuilder.buildBin(String.format("group-%s", id))
				.addElement("audiotestsrc", String.format("group-%s-silencesrc", id))
					.withProperty("wave", "silence")
					.withProperty("is-live", true)
					.withProperty("samplesperbuffer", GstConstants.SAMPLES_PER_BUFFER)
				.withCaps(Caps.fromString("audio/x-raw,format=S16LE,rate=48000,channels=1"))
				.linkElement("audiomixer", mixerName)
					.withProperty("start-time-selection", "first")
					.withProperty("output-buffer-duration", GstConstants.BUFFER_DURATION_NS)
				.linkElement("tee", teeName)
					.withProperty("allow-not-linked", true)
				.build();
		// @formatter:on

		tee = bin.getElementByName(teeName);
		mixer = bin.getElementByName(mixerName);

		expectSuccess(pipeline.add(bin));
		expectSuccess(bin.syncStateWithParent());

		debugPipeline(String.format("after-add-group-%s", id), pipeline);
		log.debug("Created Group {}", id);
	}

	public String getId() {
		return id;
	}

	void remove() {
		log.info("Removing Group {}", id);
		debugPipeline(String.format("before-remove-group-%s", id), pipeline);

		log.debug("Asking In-Clients to stop Transmitting");
		inClients.forEach(client -> client.stopTransmittingTo(this));
		inClients.clear();
		debugPipeline(String.format("after-stop-transmitting-%s", id), pipeline);

		log.debug("Asking Out-Clients to stop Receiving");
		outClients.forEach(client -> client.stopReceivingFrom(this));
		outClients.clear();
		debugPipeline(String.format("after-stop-receiving-%s", id), pipeline);

		expectSuccess(bin.stop());
		expectSuccess(pipeline.remove(bin));

		debugPipeline(String.format("after-remove-group-%s", id), pipeline);
		log.debug("Removed Group {}", id);
	}

	Pad requestSrcPadAndLinkFor(GhostPad sinkPad, Client client) {
		outClients.add(client);

		Pad teePad = tee.getRequestPad("src_%u");
		return GstPadBlock.blockAndWait(teePad, () -> {
			GhostPad ghostPad = new GhostPad(teePad.getName() + "_ghost", teePad);
			bin.addPad(ghostPad);
			ghostPad.link(sinkPad);
			return ghostPad;
		});
	}

	void releaseSrcPadFor(GhostPad pad, Client client) {
		outClients.remove(client);

		Pad teePad = pad.getTarget();
		GstPadBlock.blockAndWait(teePad, () -> {
			tee.releaseRequestPad(teePad);
			bin.removePad(pad);
		});
	}

	GhostPad requestSinkPadFor(Client client) {
		inClients.add(client);

		Pad mixerPad = mixer.getRequestPad("sink_%u");
		GhostPad ghostPad = new GhostPad(mixerPad.getName() + "_ghost", mixerPad);
		bin.addPad(ghostPad);
		return ghostPad;
	}

	void releaseSinkPadFor(GhostPad pad, Client client) {
		inClients.remove(client);

		Pad mixerPad = pad.getTarget();
		GstPadBlock.blockAndWait(mixerPad, () -> {
			mixer.releaseRequestPad(mixerPad);
			bin.removePad(pad);
		});
	}
}
