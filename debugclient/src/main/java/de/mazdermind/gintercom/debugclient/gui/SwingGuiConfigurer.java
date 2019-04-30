package de.mazdermind.gintercom.debugclient.gui;

import javax.annotation.PostConstruct;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(SwingGuiConfigurer.BEAN_NAME)
public class SwingGuiConfigurer {
	public static final String BEAN_NAME = "swingGuiConfigurer";
	private static Logger log = LoggerFactory.getLogger(SwingGuiConfigurer.class);

	@PostConstruct
	public void configure() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		log.info("Configuring UI-Framework");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame.setDefaultLookAndFeelDecorated(true);
	}
}
