package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.events.ConnectionLifecycleEvent;
import de.mazdermind.gintercom.shared.controlserver.events.support.ConnectionLifecycleEventAware;

@Component
public class ConnectionLifecycleModalManager implements ConnectionLifecycleEventAware {
	private static final Dimension INITIAL_DIMENSION = new Dimension(400, 100);
	private static final Logger log = LoggerFactory.getLogger(ConnectionLifecycleModalManager.class);
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

	public JDialog create(JFrame owner) {
		log.info("Creating");
		JDialog dialog = new JDialog(owner, Dialog.ModalityType.DOCUMENT_MODAL);
		dialog.setTitle("Not Connected to Matrix");
		dialog.setSize(INITIAL_DIMENSION);
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
		return dialog;
	}

	@Override
	public void handleGenericConnectionLifecycleEvent(ConnectionLifecycleEvent lifecycleEvent) {
		operational = lifecycleEvent.getLifecycle().isOperational();
		log.info("ConnectionLifecycleEvent: {}, Operational?: {}",
			lifecycleEvent.getClass().getSimpleName(),
			lifecycleEvent.getLifecycle().isOperational());

		EventQueue.invokeLater(() -> {
			initialDisplayText = lifecycleEvent.getDisplayText();
			initialDetailsText = lifecycleEvent.getDetailsText();
			initiallyOperational = lifecycleEvent.getLifecycle().isOperational();
			if (label != null && dialog != null) {
				label.setText(lifecycleEvent.getDisplayText());
				detailsLabel.setText(lifecycleEvent.getDetailsText());
				dialog.setVisible(!lifecycleEvent.getLifecycle().isOperational());
			}
		});
	}
}
