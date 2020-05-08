package de.mazdermind.gintercom.clientsupport.pipeline.audiosupport;

import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.ElementFactory;
import org.springframework.stereotype.Component;

@Component
public class PulseAudioSystem implements AudioSystem {
	private static final String SINK_ELEMENT = "pulsesink";
	private static final String SRC_ELEMENT = "pulsesrc";

	@Override
	public boolean available() {
		return ElementFactory.find(SRC_ELEMENT) != null &&
			ElementFactory.find(SINK_ELEMENT) != null;
	}

	@Override
	public Element buildSourceElement() {
		Element src = ElementFactory.make(SRC_ELEMENT, SRC_ELEMENT);
		src.set("client-name", "GIntercom Client (Source)");
		//src.set("blocksize", GstConstants.BYTES_PER_BUFFER);
		return src;
	}

	@Override
	public Element buildSinkElement() {
		Element sink = ElementFactory.make(SINK_ELEMENT, SINK_ELEMENT);
		sink.set("client-name", "GIntercom Client (Sink)");
		sink.set("async", false);
		sink.set("sync", false);
		return sink;
	}
}
