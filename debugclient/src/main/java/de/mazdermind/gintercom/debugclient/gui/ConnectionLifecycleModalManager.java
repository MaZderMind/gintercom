package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConnectionLifecycleModalManager {
	private static final Dimension INITIAL_DIMENSION = new Dimension(300, 100);
	private static Logger log = LoggerFactory.getLogger(ConnectionLifecycleModalManager.class);
	private JLabel label;

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
				// TODO check connection status before terminating
				log.info("Closed without successful connection - terminating Application");

				// System.exit will destroy the Spring Context correctly via a JVM Shutdown Hook
				owner.setVisible(false);
				System.exit(0);
			}
		});

		label = new JLabel("Starting Up…");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		dialog.add(label);

		return dialog;
	}
}
