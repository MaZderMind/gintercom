package de.mazdermind.gintercom.debugClient.gui;

import static de.mazdermind.gintercom.debugClient.gui.Constants.BORDER;

import java.awt.*;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class AudioLevelDisplay extends JPanel {
	private static Logger log = LoggerFactory.getLogger(AudioLevelDisplay.class);

	@Override
	protected void paintComponent(Graphics g) {
		log.debug("Painting");
		super.paintComponent(g);
		setForeground(Color.RED);
		g.fillRect(0, 0, getWidth(), getHeight() / 2);
	}

	@PostConstruct
	public void configure() {
		EventQueue.invokeLater(() -> {
			setPreferredSize(new Dimension(48, Integer.MAX_VALUE));
		});
	}
}
