package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;

import javax.annotation.PostConstruct;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SwingApplicationManager {
	private static Logger log = LoggerFactory.getLogger(SwingApplicationManager.class);

	private final BeanFactory beanFactory;
	private MainWindow mainWindow;

	public SwingApplicationManager(
		@Autowired BeanFactory beanFactory
	) {
		this.beanFactory = beanFactory;
	}

	@PostConstruct
	public void show() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		log.info("Configuring UI-Framework");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame.setDefaultLookAndFeelDecorated(true);

		EventQueue.invokeLater(() -> {
			log.info("Creating MainWindow");
			mainWindow = beanFactory.getBean(MainWindow.class);

			// EXIT_ON_CLOSE will destroy the Spring Context correctly via a JVM Shutdown Hook
			mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			log.info("Showing MainWindow");
			mainWindow.setVisible(true);
		});
	}
}
