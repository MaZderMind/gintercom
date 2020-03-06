package de.mazdermind.gintercom.mixingcore;

import static de.mazdermind.gintercom.mixingcore.support.GstDebugger.debugPipeline;
import static de.mazdermind.gintercom.mixingcore.support.GstErrorCheck.expectSuccess;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.GhostPad;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mazdermind.gintercom.mixingcore.support.GstBuilder;
import de.mazdermind.gintercom.mixingcore.support.VoidFuture;

public class Group {
	private static final Logger log = LoggerFactory.getLogger(Group.class);

	private static final int WAVE_SILENCE = 4;
	private static final int START_TIME_FIRST = 1;

	private final String name;

	private final Pipeline pipeline;
	private final Bin bin;

	private final Element tee;
	private final Element mixer;

	Group(Pipeline pipeline, String name) {
		log.info("Creating Group {}", name);

		this.name = name;
		this.pipeline = pipeline;

		String teeName = String.format("group-%s-out", name);
		String mixerName = String.format("group-%s-in", name);

		// @formatter:off
		bin = GstBuilder.buildBin(String.format("group-%s", name))
				.addElement("audiotestsrc")
					.withProperty("wave", WAVE_SILENCE)
					.withProperty("is-live", true)
				.withCaps(StaticCaps.AUDIO)
				.linkElement("audiomixer", mixerName)
					.withProperty("start-time-selection", START_TIME_FIRST) // fixes burst
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

	public void remove() {
		log.info("Removing Group {}", name);
		expectSuccess(bin.stop());
		expectSuccess(pipeline.remove(bin));

		debugPipeline(String.format("after-remove-group-%s", name), pipeline);
		log.info("Removed Group {}", name);
	}

	Pad requestSrcPad() {
		Pad teePad = tee.getRequestPad("src_%u");
		GhostPad ghostPad = new GhostPad(null, teePad);
		ghostPad.setActive(true);
		bin.addPad(ghostPad);
		return ghostPad;
	}

	void releaseSrcPad(Pad pad) {
		Pad teePad = ((GhostPad) pad).getTarget();
		VoidFuture future = new VoidFuture();
		teePad.block(() -> {
			tee.releaseRequestPad(teePad);
			bin.removePad(pad);
			future.complete();
		});
		future.await();
	}

	Pad requestSinkPad() {
		Pad mixerPad = mixer.getRequestPad("sink_%u");
		GhostPad ghostPad = new GhostPad(null, mixerPad);
		ghostPad.setActive(true);
		bin.addPad(ghostPad);
		return ghostPad;
	}

	void releaseSinkPad(Pad pad) {
		Pad mixerPad = ((GhostPad) pad).getTarget();
		VoidFuture future = new VoidFuture();
		mixerPad.block(() -> {
			mixer.releaseRequestPad(mixerPad);
			bin.removePad(pad);
			future.complete();
		});
		future.await();
	}
}
