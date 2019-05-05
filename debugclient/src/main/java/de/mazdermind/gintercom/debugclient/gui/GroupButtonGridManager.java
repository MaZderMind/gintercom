package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;
import java.math.RoundingMode;
import java.util.List;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.IntMath;

import de.mazdermind.gintercom.shared.controlserver.ClientConfiguration;

@Component
public class GroupButtonGridManager {
	private final static int COLS = 2;
	private static final Logger log = LoggerFactory.getLogger(GroupButtonGridManager.class);
	private final ClientConfiguration clientConfiguration;


	public GroupButtonGridManager(
		@Autowired ClientConfiguration clientConfiguration
	) {
		this.clientConfiguration = clientConfiguration;
	}

	@VisibleForTesting
	static int calcularNumberOfRows(int numButtons, int cols) {
		return IntMath.divide(numButtons, cols, RoundingMode.CEILING);
	}

	/**
	 * Muss be called from AWT Thread
	 */
	public JPanel create() {
		log.info("Creating");

		List<String> buttons = clientConfiguration.getButtons();
		int rows = calcularNumberOfRows(buttons.size(), COLS);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(rows, COLS, BORDER, BORDER));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, BORDER));
		panel.setPreferredSize(new Dimension(400, Integer.MAX_VALUE));

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < COLS; col++) {
				int i = row * COLS + col;
				if (i >= buttons.size()) {
					break;
				}

				panel.add(new JButton(buttons.get(i)));
			}
		}

		return panel;
	}
}
