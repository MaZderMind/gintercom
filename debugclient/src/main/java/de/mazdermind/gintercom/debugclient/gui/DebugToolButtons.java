package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;

import javax.annotation.PostConstruct;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class DebugToolButtons extends JPanel {
	private static Logger log = LoggerFactory.getLogger(DebugToolButtons.class);

	@PostConstruct
	public void configure() {
		EventQueue.invokeLater(() -> {
			log.info("Configuring GroupButtonGrid");
			setLayout(new GridLayout(4, 1, BORDER, BORDER));
			setBorder(BorderFactory.createEmptyBorder(0, BORDER, 0, 0));

			add(new JToggleButton("Generate Tone"));
			add(new JToggleButton("Speak"));
			add(new JToggleButton("Listen"));
			add(new JToggleButton("Record"));
		});
	}
}
