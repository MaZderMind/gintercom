package de.mazdermind.gintercom.matrix.pipeline;

import static de.mazdermind.gintercom.shared.pipeline.support.GstErrorCheck.expectTrue;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.GhostPad;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.shared.pipeline.StaticCaps;
import de.mazdermind.gintercom.shared.pipeline.support.ElementFactory;
import de.mazdermind.gintercom.shared.pipeline.support.GstErrorCheck;

@Component
@Scope("prototype")
public class Group {
	public static final int WAVE_SILENCE = 4;
	private static final Logger log = LoggerFactory.getLogger(Group.class);
	private final Config config;
	private Pipeline pipeline;
	private String groupId;

	private Bin bin;
	private Element mixer;
	private Element tee;

	public Group(
		@Autowired Config config
	) {
		this.config = config;
	}

	public void configure(Pipeline pipeline, String groupId, GroupConfig groupConfig) {
		log.info("configuring Bin for Group {}", groupId);

		this.pipeline = pipeline;
		this.groupId = groupId;

		bin = new ElementFactory(pipeline).createAndAddBin(String.format("group-%s", groupId));
		ElementFactory elementFactory = new ElementFactory(bin);

		Element silenceSrc = elementFactory.createAndAddElement("audiotestsrc");
		silenceSrc.set("wave", WAVE_SILENCE);
		silenceSrc.set("is-live", true);

		mixer = elementFactory.createAndAddElement("audiomixer");
		mixer.set("start-time-selection", 1);
		mixer.set("latency", config.getMatrixConfig().getRtp().getJitterbuffer() * 1_000_000 + 500_000);
		silenceSrc.linkFiltered(mixer, StaticCaps.AUDIO);

		tee = elementFactory.createAndAddElement("tee");
		tee.set("allow-not-linked", true);
		expectTrue(mixer.link(tee));

		expectTrue(bin.syncStateWithParent());

		pipeline.debugToDotFileWithTS(Bin.DebugGraphDetails.SHOW_ALL, String.format("group-%s-add", groupId));
	}

	public void deconfigure() {
		log.info("De-Configuring Bin for Group {}", groupId);

		GstErrorCheck.expectSuccess(bin.setState(State.NULL));
		expectTrue(pipeline.remove(bin));

		pipeline.debugToDotFileWithTS(Bin.DebugGraphDetails.SHOW_ALL, String.format("group-%s-remove", groupId));
	}

	public Pad requestSrcPad() {
		Pad teePad = tee.getRequestPad("src_%u");
		GhostPad ghostPad = new GhostPad(null, teePad);
		ghostPad.setActive(true);
		bin.addPad(ghostPad);
		return ghostPad;
	}

	public void releaseSrcPad(Pad ghostPad) {
		Pad teePad = ((GhostPad) ghostPad).getTarget();
		bin.removePad(ghostPad);
		tee.releaseRequestPad(teePad);
	}

	public Pad requestSinkPad() {
		Pad mixerPad = mixer.getRequestPad("sink_%u");
		GhostPad ghostPad = new GhostPad(null, mixerPad);
		ghostPad.setActive(true);
		bin.addPad(ghostPad);
		return ghostPad;
	}

	public void releaseSinkPad(Pad ghostPad) {
		Pad mixerPad = ((GhostPad) ghostPad).getTarget();
		bin.removePad(ghostPad);
		mixer.releaseRequestPad(mixerPad);
	}
}
