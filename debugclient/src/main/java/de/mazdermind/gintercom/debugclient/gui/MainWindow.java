package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn(SwingGuiConfigurer.BEAN_NAME)
public class MainWindow extends JFrame {
	private static final Dimension INITIAL_DIMENSION = new Dimension(640, 480);
	private static Logger log = LoggerFactory.getLogger(MainWindow.class);
	private final GroupButtonGrid groupButtonGrid;
	private final AudioLevelDisplay audioLevelDisplay;
	private final DebugToolButtons debugToolButtons;


	public MainWindow(
		@Autowired GroupButtonGrid groupButtonGrid,
		@Autowired AudioLevelDisplay audioLevelDisplay,
		@Autowired DebugToolButtons debugToolButtons
	) {
		super();
		this.groupButtonGrid = groupButtonGrid;
		this.audioLevelDisplay = audioLevelDisplay;
		this.debugToolButtons = debugToolButtons;
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

			add(groupButtonGrid);
			add(audioLevelDisplay);
			add(debugToolButtons);
		});
	}
}
