package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn(SwingGuiConfigurer.BEAN_NAME)
public class SwingApplicationManager {
	private static Logger log = LoggerFactory.getLogger(SwingApplicationManager.class);

	private MainWindow mainWindow;

	public SwingApplicationManager(
		@Autowired MainWindow mainWindow
	) {
		this.mainWindow = mainWindow;
	}

	@PostConstruct
	public void show() {
		EventQueue.invokeLater(() -> {
			// EXIT_ON_CLOSE will destroy the Spring Context correctly via a JVM Shutdown Hook
			mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			log.info("Showing MainWindow");
			mainWindow.setVisible(true);
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
