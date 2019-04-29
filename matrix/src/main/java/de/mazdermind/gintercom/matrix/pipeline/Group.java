package de.mazdermind.gintercom.matrix.pipeline;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.GroupConfig;
import de.mazdermind.gintercom.shared.pipeline.support.ElementFactory;

@Component
@Scope("prototype")
public class Group {
	private static Logger log = LoggerFactory.getLogger(Group.class);

	public void configure(Pipeline pipeline, String groupId, GroupConfig groupConfig) {
		log.info("Creating Pipeline-Elements for Group {}", groupId);
		Bin bin = new ElementFactory(pipeline).createAndAddBin(String.format("group-%s", groupId));

		ElementFactory factory = new ElementFactory(bin);
		Element audiomixer = factory.createAndAddElement("audiomixer", String.format("group-mixer-%s", groupId));
		Element tee = factory.createAndAddElement("tee", String.format("group-tee-%s", groupId));
		audiomixer.link(tee);
	}
}
