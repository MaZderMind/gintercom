package de.mazdermind.gintercom.mixingcore.support;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.ElementFactory;
import org.freedesktop.gstreamer.Pipeline;

public class GstBuilder<T extends Bin> {
	private final T container;
	private Element lastAddedElement;
	private Caps lastCaps;

	private GstBuilder(T container) {
		this.container = container;
	}

	public static GstBuilder<Bin> buildBin(String name) {
		return new GstBuilder<>(new Bin(name));
	}

	public static GstBuilder<Pipeline> buildPipeline(String name) {
		return new GstBuilder<>(new Pipeline(name));
	}

	public GstBuilder<T> addElement(String element) {
		return addElement(element, null);
	}

	public GstBuilder<T> addElement(String element, String name) {
		lastAddedElement = ElementFactory.make(element, name);
		container.add(lastAddedElement);
		lastCaps = null;
		return this;
	}

	public GstBuilder<T> withProperty(String name, Object value) {
		if (value instanceof String) {
			lastAddedElement.setAsString(name, (String) value);
		} else {
			lastAddedElement.set(name, value);
		}
		return this;
	}

	public GstBuilder<T> withCaps(Caps caps) {
		lastCaps = caps;
		return this;
	}

	public GstBuilder<T> linkElement(String element) {
		return linkElement(element, null);
	}

	public GstBuilder<T> linkElement(String element, String name) {
		Element nextElement = ElementFactory.make(element, name);
		container.add(nextElement);

		if (lastCaps == null) {
			lastAddedElement.link(nextElement);
		} else {
			lastAddedElement.linkFiltered(nextElement, lastCaps);
			lastCaps = null;
		}

		lastAddedElement = nextElement;
		return this;
	}

	public T build() {
		lastAddedElement = null;
		lastCaps = null;
		return container;
	}
}
