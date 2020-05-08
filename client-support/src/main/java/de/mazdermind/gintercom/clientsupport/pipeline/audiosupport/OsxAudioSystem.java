package de.mazdermind.gintercom.clientsupport.pipeline.audiosupport;

import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.ElementFactory;
import org.springframework.stereotype.Component;

@Component
public class OsxAudioSystem implements AudioSystem {
	private static final String SINK_ELEMENT = "osxaudiosink";
	private static final String SRC_ELEMENT = "osxaudiosrc";

	@Override
	public boolean available() {
		return ElementFactory.find(SINK_ELEMENT) != null &&
			ElementFactory.find(SRC_ELEMENT) != null;
	}

	@Override
	public Element buildSourceElement() {
		Element src = ElementFactory.make(SRC_ELEMENT, SRC_ELEMENT);
		//src.set("blocksize", GstConstants.BYTES_PER_BUFFER);
		return src;
	}

	@Override
	public Element buildSinkElement() {
		Element sink = ElementFactory.make(SINK_ELEMENT, SINK_ELEMENT);
		sink.set("async", false);
		sink.set("sync", false);
		return sink;
	}
}