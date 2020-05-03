package de.mazdermind.gintercom.gstreamersupport;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.ElementFactory;
import org.freedesktop.gstreamer.GhostPad;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.Pipeline;

public class GstBuilder<T extends Bin> {
	private final T container;
	private Element currentElement;
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
		return addElement(ElementFactory.make(element, name));
	}

	public GstBuilder<T> addElement(Element element) {
		currentElement = element;
		container.add(currentElement);
		lastCaps = null;
		return this;
	}

	public GstBuilder<T> withProperty(String name, Object value) {
		if (value instanceof String) {
			currentElement.setAsString(name, (String) value);
		} else {
			currentElement.set(name, value);
		}
		return this;
	}

	public GstBuilder<T> withCaps(Caps caps) {
		lastCaps = caps;
		return this;
	}

	public GstBuilder<T> linkElement(String nextElement) {
		return linkElement(nextElement, null);
	}

	public GstBuilder<T> linkElement(String nextElement, String name) {
		return linkElement(ElementFactory.make(nextElement, name));
	}

	public GstBuilder<T> linkElement(Element nextElement) {
		container.add(nextElement);
		linkWithCaps(currentElement, nextElement);

		currentElement = nextElement;
		return this;
	}

	public GstBuilder<T> linkExistingElement(String nextElementName) {
		Element nextElement = container.getElementByName(nextElementName);
		linkWithCaps(currentElement, nextElement);

		return this;
	}

	public GstBuilder<T> existingElement(String existingElementName) {
		currentElement = container.getElementByName(existingElementName);
		return this;
	}

	private void linkWithCaps(Element from, Element to) {
		if (lastCaps == null) {
			from.link(to);
		} else {
			currentElement.linkFiltered(to, lastCaps);
			lastCaps = null;
		}
	}

	public GstBuilder<T> withGhostPad(String elementName, String padName) {
		Element element = container.getElementByName(elementName);
		Pad pad = element.getStaticPad(padName);
		GhostPad ghostPad = new GhostPad(null, pad);
		container.addPad(ghostPad);
		return this;
	}

	public T build() {
		currentElement = null;
		lastCaps = null;
		return container;
	}
}
