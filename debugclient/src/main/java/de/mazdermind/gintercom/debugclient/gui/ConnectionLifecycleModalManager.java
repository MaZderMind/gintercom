package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.debugclient.gui.components.WrappingLabel;
import de.mazdermind.gintercom.shared.controlserver.ConnectionLifecycleManager;
import de.mazdermind.gintercom.shared.controlserver.events.ConnectionLifecycleEvent;

@Component
public class ConnectionLifecycleModalManager {
	private static final Dimension INITIAL_DIMENSION = new Dimension(300, 100);
	private static Logger log = LoggerFactory.getLogger(ConnectionLifecycleModalManager.class);
	private final ConnectionLifecycleManager lifecycleManager;
	private JDialog dialog;
	private JLabel label;
	private boolean operational;

	public ConnectionLifecycleModalManager(
		@Autowired ConnectionLifecycleManager lifecycleManager
	) {
		this.lifecycleManager = lifecycleManager;
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

		label = new WrappingLabel("Starting Up…");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		dialog.add(label);

		boolean isOperational = lifecycleManager.getLifecycle().isOperational();
		if (isOperational) {
			log.info("System is already Operational - skipping ConnectionLifecycleModal");
			dialog.setVisible(false);
		} else {
			log.info("Showing ConnectionLifecycleModal");
			dialog.setVisible(true);
		}

		assert this.dialog == null : "only one ConnectionLifecycleModal is supported";
		this.dialog = dialog;
		return dialog;
	}

	@EventListener
	public void handleLifecycleEvent(ConnectionLifecycleEvent lifecycleEvent) {
		operational = lifecycleEvent.getLifecycle().isOperational();
		log.info("Event: {}, Operational?: {}",
			lifecycleEvent.getClass().getSimpleName(),
			lifecycleEvent.getLifecycle().isOperational());
		EventQueue.invokeLater(() -> {
			label.setText(lifecycleEvent.getDisplayText());
			dialog.setVisible(!lifecycleEvent.getLifecycle().isOperational());
		});
	}
}
