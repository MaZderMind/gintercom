package de.mazdermind.gintercom.debugclient.gui.components;

import javax.swing.*;

import org.apache.commons.text.StringEscapeUtils;

public class WrappingLabel extends JLabel {
	public WrappingLabel() {
	}

	public WrappingLabel(String text) {
		setText(text);
	}

	@Override
	public void setText(String text) {
		super.setText("<html><center>" + StringEscapeUtils.escapeHtml4(text) + "</center></html>");
	}
}
