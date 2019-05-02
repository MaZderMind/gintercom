package de.mazdermind.gintercom.shared.controlserver.messages.ohai;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

public class Capabilities {
	@NotNull
	private List<String> buttons = new ArrayList<>();

	public List<String> getButtons() {
		return unmodifiableList(buttons);
	}

	public Capabilities setButtons(List<String> buttons) {
		this.buttons.clear();
		this.buttons.addAll(buttons);
		return this;
	}
}
