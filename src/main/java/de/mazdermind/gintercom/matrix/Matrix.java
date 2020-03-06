package de.mazdermind.gintercom.matrix;

import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mazdermind.gintercom.matrix.portpool.PortSet;

public class Matrix {
	private static final Logger log = LoggerFactory.getLogger(Matrix.class);

	private final Pipeline pipeline;

	Matrix() {
		pipeline = new Pipeline("matrix");
		pipeline.play();

		pipeline.getBus().connect((Bus.ERROR) (source, code, message) -> log.error(String.format("%s: %s", source.getName(), message)));
		pipeline.getBus().connect((Bus.WARNING) (source, code, message) -> log.warn(String.format("%s: %s", source.getName(), message)));
		pipeline.getBus().connect((Bus.EOS) source -> log.error(String.format("%s: EOS", source.getName())));
		/*pipeline.getBus().connect((source, old, current, pending) -> log.info(String.format("%s: state changed %s -> %s pending %s",
				source.getName(), old.name(), current.name(), pending.name())));*/
	}

	public Group addGroup(String name) {
		return new Group(pipeline, name);
	}

	public Panel addPanel(String name, String panelHost, PortSet portSet) {
		return new Panel(pipeline, name, panelHost, portSet);
	}
}
