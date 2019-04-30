package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;

import javax.annotation.PostConstruct;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.debugclient.gui.components.ToggleButton;
import de.mazdermind.gintercom.debugclient.pipeline.Pipeline;

@Component
@DependsOn(SwingGuiConfigurer.BEAN_NAME)
public class DebugToolButtons extends JPanel {
	private static Logger log = LoggerFactory.getLogger(DebugToolButtons.class);
	private final Pipeline pipeline;

	public DebugToolButtons(
		@Autowired Pipeline pipeline
	) {
		super();
		this.pipeline = pipeline;
	}

	@PostConstruct
	public void configure() {
		EventQueue.invokeLater(() -> {
			log.info("Configuring");
			setLayout(new GridLayout(4, 1, BORDER, BORDER));
			setBorder(BorderFactory.createEmptyBorder(0, BORDER, 0, 0));
			setPreferredSize(new Dimension(200, Integer.MAX_VALUE));

			add(new ToggleButton("Generate Tone", "Stop Generating Tone")
				.setStateChangedHandler(pipeline::configureTone));

			add(new ToggleButton("Enable Microphone", "Disable Microphone")
				.setStateChangedHandler(pipeline::configureMicrohpne));

			add(new ToggleButton("Enable Speaker", "Disable Speaker")
				.setStateChangedHandler(pipeline::configureSpeaker));
		});
	}
}
