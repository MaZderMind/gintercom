package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainWindowManager {
	private static final Dimension INITIAL_DIMENSION = new Dimension(640, 480);
	private final GroupButtonGridManager groupButtonGridManager;
	private final AudioLevelDisplayManager audioLevelDisplayManager;
	private final DebugToolButtonsManager debugToolButtonsManager;
	private JFrame mainWindow;

	/**
	 * Muss be called from AWT Thread
	 */
	public JFrame create() {
		log.info("Creating");
		mainWindow = new JFrame();
		mainWindow.setTitle("GIntercom Debug Client");
		mainWindow.setSize(INITIAL_DIMENSION);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		mainWindow.setContentPane(contentPane);

		mainWindow.add(groupButtonGridManager.create());
		mainWindow.add(audioLevelDisplayManager.create());
		mainWindow.add(debugToolButtonsManager.create());

		return mainWindow;
	}

	@EventListener
	public void handleProvisioningInformation(ProvisioningInformation provisioningInformation) {
		EventQueue.invokeLater(() -> mainWindow.setTitle(provisioningInformation.getDisplay()));
	}
}
