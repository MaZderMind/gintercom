package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.debugclient.gui.components.ToggleButton;
import de.mazdermind.gintercom.debugclient.pipeline.Pipeline;

@Component
public class DebugToolButtonsManager {
	private static final Logger log = LoggerFactory.getLogger(DebugToolButtonsManager.class);
	private final Pipeline pipeline;

	public DebugToolButtonsManager(
		@Autowired Pipeline pipeline
	) {
		super();
		this.pipeline = pipeline;
	}

	/**
	 * Muss be called from AWT Thread
	 */
	public JPanel create() {
		log.info("Creating");
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 1, BORDER, BORDER));
		panel.setBorder(BorderFactory.createEmptyBorder(0, BORDER, 0, 0));
		panel.setPreferredSize(new Dimension(200, Integer.MAX_VALUE));

		panel.add(new ToggleButton("Generate Tone", "Stop Generating Tone")
			.setStateChangedHandler(pipeline::configureTone));

		panel.add(new ToggleButton("Enable Microphone", "Disable Microphone")
			.setStateChangedHandler(pipeline::configureMicrohpne));

		panel.add(new ToggleButton("Enable Speaker", "Disable Speaker")
			.setStateChangedHandler(pipeline::configureSpeaker));

		return panel;
	}
}
