package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MainWindow extends JFrame {
	private static final Dimension INITIAL_DIMENSION = new Dimension(640, 480);
	private static Logger log = LoggerFactory.getLogger(MainWindow.class);
	private final BeanFactory beanFactory;

	public MainWindow(
		@Autowired BeanFactory beanFactory
	) {
		super();
		this.beanFactory = beanFactory;
	}

	@PostConstruct
	public void configure() {
		EventQueue.invokeLater(() -> {
			log.info("Configuring");
			setTitle("GIntercom Debug Client");
			setSize(INITIAL_DIMENSION);

			JPanel contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
			contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
			setContentPane(contentPane);

			add(beanFactory.getBean(GroupButtonGrid.class));
			add(beanFactory.getBean(AudioLevelDisplay.class));
			add(beanFactory.getBean(DebugToolButtons.class));
		});
	}
}
