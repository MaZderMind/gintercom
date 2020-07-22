package de.mazdermind.gintercom.mixingcore;

import static de.mazdermind.gintercom.gstreamersupport.GstDebugger.debugPipeline;
import static de.mazdermind.gintercom.gstreamersupport.GstErrorCheck.expectSuccess;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
import de.mazdermind.gintercom.gstreamersupport.GstStaticCaps;
import de.mazdermind.gintercom.mixingcore.exception.InvalidMixingCoreOperationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {
	private final String id;

	private final Pipeline pipeline;
	private final Bin rxBin;
	private final Bin txBin;

	private final Element tee;
	private final Element mixer;

	private final Map<Group, Pad> txPads = new HashMap<>();
	private final Map<Group, Pad> rxPads = new HashMap<>();

	Client(Pipeline pipeline, String id, InetAddress clientHost, int clientToMatrixPort, int matrixToClientPort) {
		log.info("Creating Client {}", id);
		this.id = id;
		this.pipeline = pipeline;

		String teeName = String.format("rx-%s", id);
		String mixerName = String.format("tx-%s", id);

		// @formatter:off
		rxBin = GstBuilder.buildBin(String.format("client-%s-rx", id))
				.addElement("udpsrc", String.format("client-%s-udpsrc", id))
					.withProperty("port", clientToMatrixPort)
				.withCaps(GstStaticCaps.RTP)
				.linkElement("rtpjitterbuffer", String.format("client-%s-jitterbuffer", id))
					.withProperty("latency", GstConstants.LATENCY_MS)
					.withProperty("drop-on-latency", true)
				.linkElement("rtpL16depay")
				.linkElement("audioconvert")
				.withCaps(Caps.fromString("audio/x-raw,format=S16LE,rate=48000,channels=1"))
				.linkElement("tee", teeName)
					.withProperty("allow-not-linked", true)

				.build();
		// @formatter:on

		// @formatter:off
		txBin = GstBuilder.buildBin(String.format("client-%s-tx", id))
				.addElement("audiotestsrc", String.format("client-%s-silencesrc", id))
					.withProperty("wave", "silence")
					.withProperty("is-live", true)
					.withProperty("samplesperbuffer", GstConstants.SAMPLES_PER_BUFFER)
				.withCaps(Caps.fromString("audio/x-raw,format=S16LE,rate=48000,channels=1"))
				.linkElement("audiomixer", mixerName)
					.withProperty("start-time-selection", "first")
					.withProperty("output-buffer-duration", GstConstants.BUFFER_DURATION_NS)
				.linkElement("audioconvert")
				.withCaps(GstStaticCaps.AUDIO_BE)
				.linkElement("rtpL16pay")
					.withProperty("mtu", GstConstants.MTU)
				.linkElement("udpsink")
					.withProperty("host", clientHost.getHostAddress())
					.withProperty("port", matrixToClientPort)
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

		debugPipeline(String.format("after-add-client-%s", id), pipeline);
		log.debug("Created Client {}", id);
	}

	public String getId() {
		return id;
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
		GstPadBlock.blockAndWait(teePad, () -> {
			rxBin.removePad(pad);
			tee.releaseRequestPad(teePad);
		});
	}

	private GhostPad requestSinkPad() {
		Pad mixerPad = mixer.getRequestPad("sink_%u");
		GhostPad ghostPad = new GhostPad(mixerPad.getName() + "_ghost", mixerPad);
		txBin.addPad(ghostPad);
		return ghostPad;
	}

	private void releaseSinkPad(GhostPad pad) {
		Pad mixerPad = pad.getTarget();
		GstPadBlock.blockAndWait(mixerPad, () -> {
			mixer.releaseRequestPad(mixerPad);
			txBin.removePad(pad);
		});
	}

	void remove() {
		log.info("Removing Client {}", id);
		debugPipeline(String.format("before-remove-client-%s", id), pipeline);

		log.debug("Releasing Tx-Pads");
		txPads.forEach((group, pad) -> {
			releaseSrcPad((GhostPad) pad.getPeer());
			group.releaseSinkPadFor((GhostPad) pad, this);
		});
		txPads.clear();
		debugPipeline(String.format("after-releasing-tx-client-%s", id), pipeline);

		log.debug("Releasing Rx-Pads");
		rxPads.forEach((group, pad) -> {
			Pad peer = pad.getPeer();
			group.releaseSrcPadFor((GhostPad) pad, this);
			releaseSinkPad((GhostPad) peer);
		});
		rxPads.clear();
		debugPipeline(String.format("after-releasing-rx-client-%s", id), pipeline);

		log.debug("De-Configuring Bins");
		expectSuccess(rxBin.stop());
		expectSuccess(pipeline.remove(rxBin));
		expectSuccess(txBin.stop());
		expectSuccess(pipeline.remove(txBin));
		debugPipeline(String.format("after-remove-client-%s", id), pipeline);

		log.debug("Removed Client {}", id);
	}

	public void startTransmittingTo(Group group) {
		log.info("Linking Client {} to Group {} for transmission", id, group.getId());

		Pad sinkPad = group.requestSinkPadFor(this);
		requestSrcPadAndLink(sinkPad);
		txPads.put(group, sinkPad);

		debugPipeline(String.format("after-link-client-%s-to-group-%s", id, group.getId()), pipeline);
		log.debug("Linked Client {} to Group {} for transmission", id, group.getId());
	}

	public void stopTransmittingTo(Group group) {
		log.info("Unlinking Client {} from Group {} for transmission", id, group.getId());

		Pad pad = txPads.remove(group);
		if (pad == null) {
			throw new InvalidMixingCoreOperationException(String.format(
				"Client %s not linked to Group %s for transmission", id, group.getId()));
		}

		releaseSrcPad((GhostPad) pad.getPeer());
		group.releaseSinkPadFor((GhostPad) pad, this);

		debugPipeline(String.format("after-unlink-client-%s-from-group-%s", id, group.getId()), pipeline);
		log.debug("Unlinked Client {} from Group {} for transmission", id, group.getId());
	}

	public void startReceivingFrom(Group group) {
		log.info("Linking Client {} to Group {} for receiving", id, group.getId());

		GhostPad sinkPad = requestSinkPad();
		Pad srcPad = group.requestSrcPadAndLinkFor(sinkPad, this);
		rxPads.put(group, srcPad);

		debugPipeline(String.format("after-link-group-%s-to-client-%s", group.getId(), id), pipeline);
		log.debug("Linked Client {} to Group {} for receiving", id, group.getId());
	}

	public void stopReceivingFrom(Group group) {
		log.info("Unlinking Client {} from Group {} for receiving", id, group.getId());

		Pad pad = rxPads.remove(group);
		if (pad == null) {
			throw new InvalidMixingCoreOperationException(String.format(
				"Client %s not linked from Group %s for receiving", id, group.getId()));
		}

		Pad peer = pad.getPeer();
		group.releaseSrcPadFor((GhostPad) pad, this);
		releaseSinkPad((GhostPad) peer);

		debugPipeline(String.format("after-unlink-client-%s-from-group-%s", id, group.getId()), pipeline);
		log.debug("Unlinked Client {} from Group {} for receiving", id, group.getId());
	}

	public Set<Group> getRxGroups() {
		return Collections.unmodifiableSet(rxPads.keySet());
	}

	public Set<Group> getTxGroups() {
		return Collections.unmodifiableSet(txPads.keySet());
	}
}
