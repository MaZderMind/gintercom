package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.*;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.IntMath;

import de.mazdermind.gintercom.clientapi.configuration.ButtonConfig;
import de.mazdermind.gintercom.clientapi.configuration.ClientConfiguration;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.MembershipChangeMessage;
import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ProvisionMessage;
import de.mazdermind.gintercom.clientsupport.controlserver.MembershipController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActionButtonGridManager {
	private static final int COLS = 2;
	private final ClientConfiguration clientConfiguration;
	private final MembershipController membershipController;

	private JPanel buttonPanel;
	private List<JButton> buttons;
	private Map<String, ButtonConfig> buttonConfig;

	@VisibleForTesting
	static int calculateNumberOfRows(int numButtons, int cols) {
		return IntMath.divide(numButtons, cols, RoundingMode.CEILING);
	}

	/**
	 * Muss be called from AWT Thread
	 */
	public JPanel create() {
		log.info("Creating");

		List<String> buttonNames = clientConfiguration.getButtons();
		int rows = calculateNumberOfRows(buttonNames.size(), COLS);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(rows, COLS, BORDER, BORDER));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, BORDER));
		buttonPanel.setPreferredSize(new Dimension(400, Integer.MAX_VALUE));

		buttons = createButtons();

		return buttonPanel;
	}

	private List<JButton> createButtons() {
		List<String> buttonNames = clientConfiguration.getButtons();
		int rows = calculateNumberOfRows(buttonNames.size(), COLS);

		List<JButton> buttons = new ArrayList<>();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < COLS; col++) {
				int i = row * COLS + col;
				if (i >= buttonNames.size()) {
					break;
				}

				String buttonName = buttonNames.get(i);
				ButtonConfig defaultButtonConfig = new ButtonConfig().setDisplay(buttonName);

				JButton button = createButton(buttonName);
				buttonPanel.add(button);
				buttons.add(button);
			}
		}

		return buttons;
	}

	private JButton createButton(String buttonName) {
		JButton button = new JButton(buttonName);
		ButtonModel model = button.getModel();

		final AtomicBoolean lastButtonPressedState = new AtomicBoolean(false);
		model.addChangeListener(e -> {
			boolean newButtonPressedState = model.isPressed();

			if (lastButtonPressedState.get() == newButtonPressedState) {
				return;
			}

			lastButtonPressedState.set(newButtonPressedState);

			log.info("Button {} {}", buttonName, newButtonPressedState ? "pressed" : "released");

			ButtonConfig buttonConfig = this.buttonConfig.get(buttonName);

			if (buttonConfig != null) {
				membershipController.changeMembership(
					newButtonPressedState ? MembershipChangeMessage.Change.JOIN : MembershipChangeMessage.Change.LEAVE,
					buttonConfig.getTargetType(),
					buttonConfig.getTarget());
			}
		});

		return button;
	}

	private void updateButtonNames(Map<String, ButtonConfig> buttonConfig) {
		List<String> buttonNames = clientConfiguration.getButtons();
		int rows = calculateNumberOfRows(buttonNames.size(), COLS);

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < COLS; col++) {
				int i = row * COLS + col;
				if (i >= buttonNames.size()) {
					break;
				}

				String buttonName = buttonNames.get(i);
				ButtonConfig config = buttonConfig.get(buttonName);
				JButton button = buttons.get(i);

				button.setText(config == null ? buttonName : config.getDisplay());
			}
		}
	}

	@EventListener
	public void handleProvisionMessage(ProvisionMessage provisionMessage) {
		membershipController.leaveAllJoined();
		buttonConfig = provisionMessage.getButtons();
		EventQueue.invokeLater(() -> updateButtonNames(provisionMessage.getButtons()));
	}
}
