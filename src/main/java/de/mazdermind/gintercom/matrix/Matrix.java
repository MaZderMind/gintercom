package de.mazdermind.gintercom.matrix;

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
	}

	public Group addGroup(String name) {
		return new Group(pipeline, name);
	}

	public Panel addPanel(String name, String panelHost, PortSet portSet) {
		return new Panel(pipeline, name, panelHost, portSet);
	}
}
