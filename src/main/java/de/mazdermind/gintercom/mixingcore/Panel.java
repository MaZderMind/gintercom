package de.mazdermind.gintercom.mixingcore;

import static de.mazdermind.gintercom.mixingcore.support.GstDebugger.debugPipeline;
import static de.mazdermind.gintercom.mixingcore.support.GstErrorCheck.expectSuccess;

import java.util.HashMap;
import java.util.Map;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.GhostPad;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mazdermind.gintercom.mixingcore.support.GstBuilder;
import de.mazdermind.gintercom.mixingcore.support.GstPadBlock;

public class Panel {
	private static final Logger log = LoggerFactory.getLogger(Panel.class);

	private static final int WAVE_SILENCE = 4;
	private static final int START_TIME_FIRST = 1;

	private final String name;

	private final Pipeline pipeline;
	private final Bin rxBin;
	private final Bin txBin;

	private final Element tee;
	private final Element mixer;

	private final Map<Group, Pad> txPads = new HashMap<>();
	private final Map<Group, Pad> rxPads = new HashMap<>();

	private boolean removed = false;

	Panel(Pipeline pipeline, String name, String panelHost, int panelToMatrixPort, int matrixToPanelPort) {
		log.info("Creating Panel {}", name);
		this.name = name;
		this.pipeline = pipeline;

		String teeName = String.format("rx-%s", name);
		String mixerName = String.format("tx-%s", name);

		// @formatter:off
		rxBin = GstBuilder.buildBin(String.format("panel-%s-rx", name))
				.addElement("udpsrc", String.format("panel-%s-udpsrc", name))
					.withProperty("port", panelToMatrixPort)
				.withCaps(StaticCaps.RTP)
				.linkElement("rtpjitterbuffer", String.format("panel-%s-jitterbuffer", name))
					.withProperty("latency", 100)
					.withProperty("drop-on-latency", true)
				.linkElement("rtpL16depay")
				.linkElement("audioconvert")
				.withCaps(StaticCaps.AUDIO)
				.linkElement("tee", teeName)
					.withProperty("allow-not-linked", true)

				.build();
		// @formatter:on

		// @formatter:off
		txBin = GstBuilder.buildBin(String.format("panel-%s-tx", name))
				.addElement("audiotestsrc", String.format("panel-%s-silencesrc", name))
					.withProperty("wave", WAVE_SILENCE)
					.withProperty("is-live", true)
				.withCaps(StaticCaps.AUDIO)
				.linkElement("audiomixer", mixerName)
					.withProperty("start-time-selection", START_TIME_FIRST) // fixes burst
				.linkElement("audioconvert")
				.withCaps(StaticCaps.AUDIO_BE)
				.linkElement("rtpL16pay")
				.linkElement("udpsink")
					.withProperty("host", panelHost)
					.withProperty("port", matrixToPanelPort)
					.withProperty("async", false)
					.withProperty("sync", false)

				.build();
		// @formatter:on

		tee = rxBin.getElementByName(teeName);
		mixer = txBin.getElementByName(mixerName);

		expectSuccess(pipeline.add(rxBin));
		expectSuccess(rxBin.syncStateWithParent());

		expectSuccess(pipeline.add(txBin));
		expectSuccess(txBin.syncStateWithParent());

		debugPipeline(String.format("after-add-panel-%s", name), pipeline);
		log.info("Created Panel {}", name);
	}

	private Pad requestSrcPadAndLink(Pad sinkPad) {
		Pad teePad = tee.getRequestPad("src_%u");
		return GstPadBlock.blockAndWait(teePad, () -> {
			GhostPad ghostPad = new GhostPad(teePad.getName() + "_ghost", teePad);
			rxBin.addPad(ghostPad);
			ghostPad.link(sinkPad);
			return ghostPad;
		});
	}

	private void releaseSrcPad(Pad pad) {
		Pad teePad = ((GhostPad) pad).getTarget();
		log.info("blocking for releaseSrcPad {}", pad);
		GstPadBlock.blockAndWait(teePad, () -> {
			log.info("blocked for releaseSrcPad {}", pad);
			rxBin.removePad(pad);
			tee.releaseRequestPad(teePad);
		});
		log.info("after blocking for releaseSrcPad {}", pad);
	}

	private Pad requestSinkPad() {
		Pad mixerPad = mixer.getRequestPad("sink_%u");
		GhostPad ghostPad = new GhostPad(mixerPad.getName() + "_ghost", mixerPad);
		txBin.addPad(ghostPad);
		return ghostPad;
	}

	private void releaseSinkPad(Pad pad) {
		Pad mixerPad = ((GhostPad) pad).getTarget();
		log.info("blocking for releaseSinkPad {}", pad);
		GstPadBlock.blockAndWait(mixerPad, () -> {
			log.info("blocked for releaseSinkPad {}", pad);
			mixer.releaseRequestPad(mixerPad);
			txBin.removePad(pad);
		});
		log.info("after blocking for releaseSinkPad {}", pad);
	}

	public void remove() {
		if (removed) {
			log.warn("Panel {} already removed", name);
			return;
		}
		log.info("Removing Panel {}", name);
		debugPipeline(String.format("before-remove-panel-%s", name), pipeline);

		log.info("Releasing Tx-Pads");
		txPads.forEach((group, pad) -> {
			releaseSrcPad(pad.getPeer());
			group.releaseSinkPad(pad);
		});
		txPads.clear();
		debugPipeline(String.format("after-releasing-tx-panel-%s", name), pipeline);

		log.info("Releasing Rx-Pads");
		rxPads.forEach((group, pad) -> {
			Pad peer = pad.getPeer();
			group.releaseSrcPad(pad);
			releaseSinkPad(peer);
		});
		rxPads.clear();
		debugPipeline(String.format("after-releasing-rx-panel-%s", name), pipeline);

		log.info("De-Configuring Bins");
		expectSuccess(rxBin.stop());
		expectSuccess(pipeline.remove(rxBin));
		expectSuccess(txBin.stop());
		expectSuccess(pipeline.remove(txBin));
		debugPipeline(String.format("after-remove-panel-%s", name), pipeline);

		log.info("Removed Panel {}", name);
		removed = true;
	}

	public void startTransmittingTo(Group group) {
		log.info("Linking Panel {} to Group {} for transmission", name, group.getName());

		Pad sinkPad = group.requestSinkPad();
		requestSrcPadAndLink(sinkPad);
		txPads.put(group, sinkPad);

		debugPipeline(String.format("after-link-panel-%s-to-group-%s", name, group.getName()), pipeline);
		log.info("Linked Panel {} to Group {} for transmission", name, group.getName());
	}

	public void stopTransmittingTo(Group group) {
		log.info("Unlinking Panel {} from Group {} for transmission", name, group.getName());

		Pad pad = txPads.remove(group);
		releaseSrcPad(pad.getPeer());
		group.releaseSinkPad(pad);

		debugPipeline(String.format("after-unlink-panel-%s-from-group-%s", name, group.getName()), pipeline);
		log.info("Unlinked Panel {} from Group {} for transmission", name, group.getName());
	}

	public void startReceivingFrom(Group group) {
		log.info("Linking Panel {} to Group {} for receiving", name, group.getName());

		Pad sinkPad = requestSinkPad();
		Pad srcPad = group.requestSrcPadAndLink(sinkPad);
		rxPads.put(group, srcPad);

		debugPipeline(String.format("after-link-group-%s-to-panel-%s", group.getName(), name), pipeline);
		log.info("Linked Panel {} to Group {} for receiving", name, group.getName());
	}

	public void stopReceivingFrom(Group group) {
		log.info("Unlinking Panel {} from Group {} for receiving", name, group.getName());

		Pad pad = rxPads.remove(group);
		Pad peer = pad.getPeer();
		group.releaseSrcPad(pad);
		releaseSinkPad(peer);

		debugPipeline(String.format("after-unlink-panel-%s-from-group-%s", name, group.getName()), pipeline);
		log.info("Unlinked Panel {} from Group {} for transmission", name, group.getName());
	}
}
