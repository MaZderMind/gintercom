package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;

import javax.swing.*;

import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.debugclient.gui.components.ToggleButton;
import de.mazdermind.gintercom.debugclient.pipeline.DebugClientPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebugToolButtonsManager {
	private final DebugClientPipeline pipeline;

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
			.setStateChangedHandler(pipeline::configureMicrophone));

		panel.add(new ToggleButton("Enable Speaker", "Disable Speaker")
			.setStateChangedHandler(pipeline::configureSpeaker));

		return panel;
	}
}
