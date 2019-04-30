package de.mazdermind.gintercom.debugclient.gui.components;

import java.awt.event.ItemEvent;
import java.util.function.Consumer;

import javax.swing.*;

public class ToggleButton extends JToggleButton {
	private Consumer<Boolean> stateChangedHandler;

	public ToggleButton(String inactiveText, String activeText) {
		setText(inactiveText);

		addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				setText(activeText);
				callStateChangedHandler(true);
			} else if (event.getStateChange() == ItemEvent.DESELECTED) {
				setText(inactiveText);
				callStateChangedHandler(false);
			}
		});
	}

	private void callStateChangedHandler(boolean state) {
		if (stateChangedHandler != null) {
			stateChangedHandler.accept(state);
		}
	}

	public ToggleButton setStateChangedHandler(Consumer<Boolean> consumer) {
		this.stateChangedHandler = consumer;
		return this;
	}
}
