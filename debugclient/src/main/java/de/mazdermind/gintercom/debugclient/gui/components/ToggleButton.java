package de.mazdermind.gintercom.debugclient.gui.components;

import java.awt.event.ItemEvent;

import javax.swing.*;

import de.mazdermind.gintercom.debugclient.util.EventEmitter;

public class ToggleButton extends JToggleButton {
	private final EventEmitter<Boolean> stateChangedEventEmitter = new EventEmitter<>();

	public ToggleButton(String inactiveText, String activeText) {
		setText(inactiveText);

		addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				setText(activeText);
				stateChangedEventEmitter.emit(true);
			} else if (event.getStateChange() == ItemEvent.DESELECTED) {
				setText(inactiveText);
				stateChangedEventEmitter.emit(false);
			}
		});
	}

	public EventEmitter<Boolean> getStateChangedEventEmitter() {
		return stateChangedEventEmitter;
	}
}
