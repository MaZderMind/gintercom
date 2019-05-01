package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GroupButtonGridManager {
	private final static int ROWS = 3;
	private final static int COLS = 2;
	private static Logger log = LoggerFactory.getLogger(GroupButtonGridManager.class);

	/**
	 * Muss be called from AWT Thread
	 */
	public JPanel create() {
		log.info("Creating");
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(ROWS, COLS, BORDER, BORDER));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, BORDER));
		panel.setPreferredSize(new Dimension(400, Integer.MAX_VALUE));

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				int i = row * COLS + col;
				panel.add(new JButton(Integer.toString(i)));
			}
		}

		return panel;
	}
}
