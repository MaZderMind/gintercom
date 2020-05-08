package de.mazdermind.gintercom.clientsupport.pipeline.audiosupport;

import org.freedesktop.gstreamer.Element;

public interface AudioSystem {
	boolean available();

	Element buildSourceElement();

	Element buildSinkElement();
}
