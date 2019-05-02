package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GuiManager {
	private static Logger log = LoggerFactory.getLogger(GuiManager.class);

	private final ConnectionLifecycleModalManager connectionLifecycleModalManager;
	private MainWindowManager mainWindowManager;
	private JFrame mainWindow;

	public GuiManager(
		@Autowired ConnectionLifecycleModalManager connectionLifecycleModalManager,
		@Autowired MainWindowManager mainWindowManager
	) {
		this.connectionLifecycleModalManager = connectionLifecycleModalManager;
		this.mainWindowManager = mainWindowManager;
	}

	@PostConstruct
	public void show() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		log.info("Configuring UI-Framework");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame.setDefaultLookAndFeelDecorated(true);

		EventQueue.invokeLater(() -> {
			log.info("Showing MainWindow");
			mainWindow = mainWindowManager.create();
			mainWindow.setLocationRelativeTo(null);

			// EXIT_ON_CLOSE will destroy the Spring Context correctly via a JVM Shutdown Hook
			mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			mainWindow.setVisible(true);

			connectionLifecycleModalManager.create(mainWindow);
		});
	}

	@PreDestroy
	public void dispose() {
		EventQueue.invokeLater(() -> {
			log.info("Application Shutting down, disposing MainWindow");
			mainWindow.dispose();
		});
	}
}
