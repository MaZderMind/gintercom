package de.mazdermind.gintercom.mixingcore;

import static de.mazdermind.gintercom.gstreamersupport.GstDebugger.debugPipeline;
import static de.mazdermind.gintercom.gstreamersupport.GstErrorCheck.expectSuccess;

import java.util.HashSet;
import java.util.Set;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.GhostPad;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.Pipeline;

import de.mazdermind.gintercom.gstreamersupport.GstBuilder;
import de.mazdermind.gintercom.gstreamersupport.GstConstants;
import de.mazdermind.gintercom.gstreamersupport.GstPadBlock;
import de.mazdermind.gintercom.gstreamersupport.GstStaticCaps;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Group {
	private final String name;

	private final Pipeline pipeline;
	private final Bin bin;

	private final Element tee;
	private final Element mixer;

	private final Set<Panel> inPanels = new HashSet<>();
	private final Set<Panel> outPanels = new HashSet<>();

	Group(Pipeline pipeline, String name) {
		log.info("Creating Group {}", name);

		this.name = name;
		this.pipeline = pipeline;

		String teeName = String.format("group-%s-out", name);
		String mixerName = String.format("group-%s-in", name);

		// @formatter:off
		bin = GstBuilder.buildBin(String.format("group-%s", name))
				.addElement("audiotestsrc", String.format("group-%s-silencesrc", name))
					.withProperty("wave", "silence")
					.withProperty("is-live", true)
				.withCaps(GstStaticCaps.AUDIO)
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

		debugPipeline(String.format("after-add-group-%s", name), pipeline);
		log.info("Created Group {}", name);
	}

	public String getName() {
		return name;
	}

	void remove() {
		log.info("Removing Group {}", name);
		debugPipeline(String.format("before-remove-group-%s", name), pipeline);

		log.info("Asking In-Panels to stop Transmitting");
		inPanels.forEach(panel -> panel.stopTransmittingTo(this));
		inPanels.clear();
		debugPipeline(String.format("after-stop-transmitting-%s", name), pipeline);

		log.info("Asking Out-Panels to stop Receiving");
		outPanels.forEach(panel -> panel.stopReceivingFrom(this));
		outPanels.clear();
		debugPipeline(String.format("after-stop-receiving-%s", name), pipeline);

		expectSuccess(bin.stop());
		expectSuccess(pipeline.remove(bin));

		debugPipeline(String.format("after-remove-group-%s", name), pipeline);
		log.info("Removed Group {}", name);
	}

	Pad requestSrcPadAndLinkFor(GhostPad sinkPad, Panel panel) {
		outPanels.add(panel);

		Pad teePad = tee.getRequestPad("src_%u");
		return GstPadBlock.blockAndWait(teePad, () -> {
			GhostPad ghostPad = new GhostPad(teePad.getName() + "_ghost", teePad);
			bin.addPad(ghostPad);
			ghostPad.link(sinkPad);
			return ghostPad;
		});
	}

	void releaseSrcPadFor(GhostPad pad, Panel panel) {
		outPanels.remove(panel);

		Pad teePad = pad.getTarget();
		log.info("blocking for releaseSrcPad {}", pad);
		GstPadBlock.blockAndWait(teePad, () -> {
			log.info("blocked for releaseSrcPad {}", pad);
			tee.releaseRequestPad(teePad);
			bin.removePad(pad);
		});
		log.info("after blocking for releaseSrcPad {}", pad);
	}

	GhostPad requestSinkPadFor(Panel panel) {
		inPanels.add(panel);

		Pad mixerPad = mixer.getRequestPad("sink_%u");
		GhostPad ghostPad = new GhostPad(mixerPad.getName() + "_ghost", mixerPad);
		bin.addPad(ghostPad);
		return ghostPad;
	}

	void releaseSinkPadFor(GhostPad pad, Panel panel) {
		inPanels.remove(panel);

		Pad mixerPad = pad.getTarget();
		log.info("blocking for releaseSinkPad {}", pad);
		GstPadBlock.blockAndWait(mixerPad, () -> {
			log.info("blocked for releaseSinkPad {}", pad);
			mixer.releaseRequestPad(mixerPad);
			bin.removePad(pad);
		});
		log.info("after blocking for releaseSinkPad {}", pad);
	}
}
