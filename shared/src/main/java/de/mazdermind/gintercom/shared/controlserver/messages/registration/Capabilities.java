package de.mazdermind.gintercom.shared.controlserver.messages.registration;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

public class Capabilities {
	@NotEmpty
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
