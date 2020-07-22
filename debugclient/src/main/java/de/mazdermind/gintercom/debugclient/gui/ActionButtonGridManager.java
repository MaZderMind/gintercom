package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.IntMath;

import de.mazdermind.gintercom.clientapi.configuration.ButtonAction;
import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.ClientConfiguration;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ProvisionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActionButtonGridManager {
	private final static int COLS = 2;

	private final ClientConfiguration clientConfiguration;
	private JPanel buttonPanel;

	@VisibleForTesting
	static int calculateNumberOfRows(int numButtons, int cols) {
		return IntMath.divide(numButtons, cols, RoundingMode.CEILING);
	}

	/**
	 * Muss be called from AWT Thread
	 */
	public JPanel create() {
		log.info("Creating");

		List<String> buttons = clientConfiguration.getButtons();
		int rows = calculateNumberOfRows(buttons.size(), COLS);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(rows, COLS, BORDER, BORDER));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, BORDER));
		buttonPanel.setPreferredSize(new Dimension(400, Integer.MAX_VALUE));

		configureButtons(Collections.emptyMap());

		return buttonPanel;
	}

	private void configureButtons(Map<String, ButtonConfig> buttonConfigMap) {
		List<String> buttons = clientConfiguration.getButtons();
		int rows = calculateNumberOfRows(buttons.size(), COLS);

		clearButtonPanel();

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < COLS; col++) {
				int i = row * COLS + col;
				if (i >= buttons.size()) {
					break;
				}

				String buttonName = buttons.get(i);
				ButtonConfig defaultButtonconfig = new ButtonConfig().setDisplay(buttonName);
				ButtonConfig buttonConfig = buttonConfigMap.getOrDefault(buttonName, defaultButtonconfig);

				buttonPanel.add(createButton(buttonConfig));
			}
		}

		buttonPanel.revalidate();
		buttonPanel.repaint();
	}

	private void clearButtonPanel() {
		for (java.awt.Component component : buttonPanel.getComponents()) {
			buttonPanel.remove(component);
		}
	}

	private AbstractButton createButton(ButtonConfig buttonConfig) {
		return buttonConfig.getAction() == ButtonAction.PUSH ?
			new JButton(buttonConfig.getDisplay()) :
			new JToggleButton(buttonConfig.getDisplay());
	}

	@EventListener
	public void handleProvisionMessage(ProvisionMessage provisionMessage) {
		EventQueue.invokeLater(() -> configureButtons(provisionMessage.getButtons()));
	}
}
