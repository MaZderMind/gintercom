package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.messages.provision.ProvisioningInformation;
import de.mazdermind.gintercom.clientsupport.controlserver.provisioning.ProvisioningInformationAware;

@Component
public class MainWindowManager implements ProvisioningInformationAware {
	private static final Dimension INITIAL_DIMENSION = new Dimension(640, 480);
	private static final Logger log = LoggerFactory.getLogger(MainWindowManager.class);
	private final GroupButtonGridManager groupButtonGridManager;
	private final AudioLevelDisplayManager audioLevelDisplayManager;
	private final DebugToolButtonsManager debugToolButtonsManager;
	private JFrame mainWindow;


	public MainWindowManager(
		@Autowired GroupButtonGridManager groupButtonGridManager,
		@Autowired AudioLevelDisplayManager audioLevelDisplayManager,
		@Autowired DebugToolButtonsManager debugToolButtonsManager
	) {
		super();
		this.groupButtonGridManager = groupButtonGridManager;
		this.audioLevelDisplayManager = audioLevelDisplayManager;
		this.debugToolButtonsManager = debugToolButtonsManager;
	}

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

	@Override
	public void handleProvisioningInformation(ProvisioningInformation provisioningInformation) {
		EventQueue.invokeLater(() -> {
			mainWindow.setTitle(provisioningInformation.getDisplay());
		});
	}
}
