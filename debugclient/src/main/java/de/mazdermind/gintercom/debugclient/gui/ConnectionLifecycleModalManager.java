package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.controlserver.messages.matrix.to.client.ErrorMessage;
import de.mazdermind.gintercom.clientsupport.events.connectionlifecycle.ConnectionLifecycleEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConnectionLifecycleModalManager {
	private static final Dimension INITIAL_DIMENSION = new Dimension(400, 100);

	private static final String TITLE_NOT_CONNECTED = "Not Connected to Matrix";
	private static final String TITLE_CONNECTED = "Connected to Matrix";
	private static final String TITLE_ERROR = "Error Message Received";

	private JDialog dialog;
	private JLabel label;
	private JLabel detailsLabel;
	private boolean operational;

	private String initialDisplayText = "Starting Up…";
	private String initialDetailsText = "";
	private boolean initiallyOperational = false;

	public ConnectionLifecycleModalManager() {
		log.info("Constructed");
	}

	public void create(JFrame owner) {
		log.info("Creating");
		JDialog dialog = new JDialog(owner, Dialog.ModalityType.DOCUMENT_MODAL);
		dialog.setTitle(TITLE_NOT_CONNECTED);
		dialog.setSize(INITIAL_DIMENSION);
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(owner);
		dialog.setModal(true);

		dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		dialog.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				if (operational) {
					return;
				}

				log.info("Closed without successful connection - terminating Application");

				// System.exit will destroy the Spring Context correctly via a JVM Shutdown Hook
				owner.setVisible(false);
				System.exit(0);
			}
		});

		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
		label = new JLabel(initialDisplayText);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		detailsLabel = new JLabel(initialDetailsText);
		detailsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		dialog.add(Box.createVerticalGlue());
		dialog.add(label, java.awt.Component.CENTER_ALIGNMENT);
		dialog.add(detailsLabel, java.awt.Component.CENTER_ALIGNMENT);
		dialog.add(Box.createVerticalGlue());

		if (operational) {
			log.info("System is already Operational - skipping ConnectionLifecycleModal");
			dialog.setVisible(false);
		} else {
			log.info("Showing ConnectionLifecycleModal");
			// setVisible(true) is actually blocking (who knew from the name…)
			EventQueue.invokeLater(() -> dialog.setVisible(!initiallyOperational));
		}

		assert this.dialog == null : "only one ConnectionLifecycleModal is supported";
		this.dialog = dialog;
	}

	@EventListener
	public void handleErrorMessage(ErrorMessage errorMessage) {
		updateModalText("Error", errorMessage.getMessage(), TITLE_ERROR, true);
	}

	@EventListener
	public void handleGenericConnectionLifecycleEvent(ConnectionLifecycleEvent lifecycleEvent) {
		operational = lifecycleEvent.getLifecycle().isOperational();
		boolean connected = lifecycleEvent.getLifecycle().isConnected();

		log.info("ConnectionLifecycleEvent: {}, Operational={}",
			lifecycleEvent.getClass().getSimpleName(), operational);

		String displayText = lifecycleEvent.getDisplayText();
		String detailsText = lifecycleEvent.getDetailsText();
		String dialogTitle = connected ? TITLE_CONNECTED : TITLE_NOT_CONNECTED;

		initialDisplayText = displayText;
		initialDetailsText = detailsText;
		initiallyOperational = operational;

		boolean shouldBeVisible = !operational;
		updateModalText(displayText, detailsText, dialogTitle, shouldBeVisible);
	}

	private void updateModalText(String displayText, String detailsText, String dialogTitle, boolean shouldBeVisible) {
		EventQueue.invokeLater(() -> {
			if (label != null && dialog != null) {
				dialog.setTitle(dialogTitle);
				label.setText(displayText);
				detailsLabel.setText(detailsText);

				// Invoking setVisible, even when the window is already visible,
				// will give it he System-Wide Focus, which is quite annoying
				if (dialog.isVisible() != shouldBeVisible) {
					dialog.setVisible(shouldBeVisible);
				}
			}
		});
	}
}
