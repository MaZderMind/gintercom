package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;

import javax.annotation.PreDestroy;
import javax.swing.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GuiManager {
	private final ConnectionLifecycleModalManager connectionLifecycleModalManager;
	private final MainWindowManager mainWindowManager;
	private JFrame mainWindow;

	public GuiManager(
		@Autowired ConnectionLifecycleModalManager connectionLifecycleModalManager,
		@Autowired MainWindowManager mainWindowManager
	) {
		this.connectionLifecycleModalManager = connectionLifecycleModalManager;
		this.mainWindowManager = mainWindowManager;
	}

	@EventListener(ContextRefreshedEvent.class)
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
