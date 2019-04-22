package de.mazdermind.gintercom.matrix.pipeline.support;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Element;

public class ElementFactory {
	private final Bin parentBin;

	public ElementFactory(Bin parentBin) {
		this.parentBin = parentBin;
	}

	public Element createAndAddElement(String factory) {
		return createAndAddElement(factory, null);
	}

	public Element createAndAddElement(String factory, String name) {
		Element element = org.freedesktop.gstreamer.ElementFactory.make(factory, name);
		parentBin.add(element);
		return element;
	}

	public Bin createAndAddBin(String name) {
		Bin bin = new Bin(name);
		parentBin.add(bin);
		return bin;
	}

	public Element createAndAddCapsfilter(String caps) {
		return createAndAddCapsfilter(Caps.fromString(caps));
	}

	public Element createAndAddCapsfilter(Caps caps) {
		Element capsfilter = createAndAddElement("capsfilter");
		capsfilter.set("caps", caps);
		parentBin.add(capsfilter);
		return capsfilter;
	}
}
