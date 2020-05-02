package de.mazdermind.gintercom.mixingcore;

import static de.mazdermind.gintercom.gstreamersupport.GstDebugger.debugPipeline;
import static de.mazdermind.gintercom.gstreamersupport.GstErrorCheck.expectSuccess;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.GhostPad;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mazdermind.gintercom.mixingcore.exception.InvalidMixingCoreOperationException;
import de.mazdermind.gintercom.gstreamersupport.GstBuilder;
import de.mazdermind.gintercom.gstreamersupport.GstPadBlock;

public class Panel {
	private static final Logger log = LoggerFactory.getLogger(Panel.class);

	private final String name;

	private final Pipeline pipeline;
	private final Bin rxBin;
	private final Bin txBin;

	private final Element tee;
	private final Element mixer;

	private final Map<Group, Pad> txPads = new HashMap<>();
	private final Map<Group, Pad> rxPads = new HashMap<>();

	Panel(Pipeline pipeline, String name, InetAddress panelHost, int panelToMatrixPort, int matrixToPanelPort) {
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
					.withProperty("latency", Constants.LATENCY_MS)
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
					.withProperty("wave", "silence")
					.withProperty("is-live", true)
					.withProperty("samplesperbuffer", Constants.SAMPLES_PER_BUFFER)
				.withCaps(StaticCaps.AUDIO)
				.linkElement("audiomixer", mixerName)
					.withProperty("start-time-selection", "first")
					.withProperty("output-buffer-duration", Constants.BUFFER_DURATION_NS)
				.linkElement("audioconvert")
				.withCaps(StaticCaps.AUDIO_BE)
				.linkElement("rtpL16pay")
					.withProperty("mtu", Constants.MTU)
				.linkElement("udpsink")
					.withProperty("host", panelHost.getHostAddress())
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

	public String getName() {
		return name;
	}

	private GhostPad requestSrcPadAndLink(Pad sinkPad) {
		Pad teePad = tee.getRequestPad("src_%u");
		return GstPadBlock.blockAndWait(teePad, () -> {
			GhostPad ghostPad = new GhostPad(teePad.getName() + "_ghost", teePad);
			rxBin.addPad(ghostPad);
			ghostPad.link(sinkPad);
			return ghostPad;
		});
	}

	private void releaseSrcPad(GhostPad pad) {
		Pad teePad = pad.getTarget();
		log.info("blocking for releaseSrcPad {}", pad);
		GstPadBlock.blockAndWait(teePad, () -> {
			log.info("blocked for releaseSrcPad {}", pad);
			rxBin.removePad(pad);
			tee.releaseRequestPad(teePad);
		});
		log.info("after blocking for releaseSrcPad {}", pad);
	}

	private GhostPad requestSinkPad() {
		Pad mixerPad = mixer.getRequestPad("sink_%u");
		GhostPad ghostPad = new GhostPad(mixerPad.getName() + "_ghost", mixerPad);
		txBin.addPad(ghostPad);
		return ghostPad;
	}

	private void releaseSinkPad(GhostPad pad) {
		Pad mixerPad = pad.getTarget();
		log.info("blocking for releaseSinkPad {}", pad);
		GstPadBlock.blockAndWait(mixerPad, () -> {
			log.info("blocked for releaseSinkPad {}", pad);
			mixer.releaseRequestPad(mixerPad);
			txBin.removePad(pad);
		});
		log.info("after blocking for releaseSinkPad {}", pad);
	}

	void remove() {
		log.info("Removing Panel {}", name);
		debugPipeline(String.format("before-remove-panel-%s", name), pipeline);

		log.info("Releasing Tx-Pads");
		txPads.forEach((group, pad) -> {
			releaseSrcPad((GhostPad) pad.getPeer());
			group.releaseSinkPadFor((GhostPad) pad, this);
		});
		txPads.clear();
		debugPipeline(String.format("after-releasing-tx-panel-%s", name), pipeline);

		log.info("Releasing Rx-Pads");
		rxPads.forEach((group, pad) -> {
			Pad peer = pad.getPeer();
			group.releaseSrcPadFor((GhostPad) pad, this);
			releaseSinkPad((GhostPad) peer);
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
	}

	public void startTransmittingTo(Group group) {
		log.info("Linking Panel {} to Group {} for transmission", name, group.getName());

		Pad sinkPad = group.requestSinkPadFor(this);
		requestSrcPadAndLink(sinkPad);
		txPads.put(group, sinkPad);

		debugPipeline(String.format("after-link-panel-%s-to-group-%s", name, group.getName()), pipeline);
		log.info("Linked Panel {} to Group {} for transmission", name, group.getName());
	}

	public void stopTransmittingTo(Group group) {
		log.info("Unlinking Panel {} from Group {} for transmission", name, group.getName());

		Pad pad = txPads.remove(group);
		if (pad == null) {
			throw new InvalidMixingCoreOperationException(String.format(
				"Panel %s not linked to Group %s for transmission", name, group.getName()));
		}

		releaseSrcPad((GhostPad) pad.getPeer());
		group.releaseSinkPadFor((GhostPad) pad, this);

		debugPipeline(String.format("after-unlink-panel-%s-from-group-%s", name, group.getName()), pipeline);
		log.info("Unlinked Panel {} from Group {} for transmission", name, group.getName());
	}

	public void startReceivingFrom(Group group) {
		log.info("Linking Panel {} to Group {} for receiving", name, group.getName());

		GhostPad sinkPad = requestSinkPad();
		Pad srcPad = group.requestSrcPadAndLinkFor(sinkPad, this);
		rxPads.put(group, srcPad);

		debugPipeline(String.format("after-link-group-%s-to-panel-%s", group.getName(), name), pipeline);
		log.info("Linked Panel {} to Group {} for receiving", name, group.getName());
	}

	public void stopReceivingFrom(Group group) {
		log.info("Unlinking Panel {} from Group {} for receiving", name, group.getName());

		Pad pad = rxPads.remove(group);
		if (pad == null) {
			throw new InvalidMixingCoreOperationException(String.format(
				"Panel %s not linked from Group %s for receiving", name, group.getName()));
		}

		Pad peer = pad.getPeer();
		group.releaseSrcPadFor((GhostPad) pad, this);
		releaseSinkPad((GhostPad) peer);

		debugPipeline(String.format("after-unlink-panel-%s-from-group-%s", name, group.getName()), pipeline);
		log.info("Unlinked Panel {} from Group {} for receiving", name, group.getName());
	}
}
