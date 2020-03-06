package de.mazdermind.gintercom.mixingcore;

import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MixingCore {
	private static final Logger log = LoggerFactory.getLogger(MixingCore.class);

	private final Pipeline pipeline;

	MixingCore() {
		pipeline = new Pipeline("matrix");
		pipeline.play();

		pipeline.getBus().connect((Bus.ERROR) (source, code, message) -> log.error(String.format("%s: %s", source.getName(), message)));
		pipeline.getBus().connect((Bus.WARNING) (source, code, message) -> log.warn(String.format("%s: %s", source.getName(), message)));
		pipeline.getBus().connect((Bus.EOS) source -> log.error(String.format("%s: EOS", source.getName())));
	}

	public Group addGroup(String name) {
		return new Group(pipeline, name);
	}

	public Panel addPanel(String name, String panelHost, int panelToMatrixPort, int matrixToPanelPort) {
		return new Panel(pipeline, name, panelHost, panelToMatrixPort, matrixToPanelPort);
	}
}
