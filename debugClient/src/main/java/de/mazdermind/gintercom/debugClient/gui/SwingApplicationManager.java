package de.mazdermind.gintercom.debugClient.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SwingApplicationManager {
	private static Logger log = LoggerFactory.getLogger(SwingApplicationManager.class);

	private final ConfigurableApplicationContext applicationContext;
	private final BeanFactory beanFactory;
	private MainWindow mainWindow;

	public SwingApplicationManager(
		@Autowired ConfigurableApplicationContext applicationContext,
		@Autowired BeanFactory beanFactory
	) {
		this.applicationContext = applicationContext;
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
			mainWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			mainWindow.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					log.info("MainWindow closed, shutting down Application");
					applicationContext.stop();
				}
			});

			log.info("Showing MainWindow");
			mainWindow.setVisible(true);
		});
	}

	@PreDestroy
	public void destroy() {
		log.info("Application closed, disposing MainWindow");
		if (mainWindow != null) {
			mainWindow.dispose();
		}
	}
}
