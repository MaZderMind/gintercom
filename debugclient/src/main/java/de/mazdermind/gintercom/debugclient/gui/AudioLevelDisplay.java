package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.debugclient.pipeline.audiolevel.AudioLevelEvent;
import de.mazdermind.gintercom.debugclient.pipeline.audiolevel.AudioLevelMessageListener;

@Component
@Scope("prototype")
public class AudioLevelDisplay extends JPanel {
	private static Logger log = LoggerFactory.getLogger(AudioLevelDisplay.class);
	private final AtomicReference<AudioLevelEvent> lastAudioLevelEvent = new AtomicReference<>();

	public AudioLevelDisplay(@Autowired AudioLevelMessageListener audioLevelMessageListener) {
		super();
		log.debug("Subscribing for Audio-Level Events");
		audioLevelMessageListener.getAudioLevelEventEmitter().subscribe(this::audioLevelEventHandler);
	}

	private void audioLevelEventHandler(AudioLevelEvent audioLevelEvent) {
		log.debug("Received Audio-Level Event");
		lastAudioLevelEvent.set(audioLevelEvent);
		EventQueue.invokeLater(() -> {
			this.invalidate();
			this.repaint();
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		AudioLevelEvent audioLevelEvent = lastAudioLevelEvent.get();
		log.debug("Painting {} on {}", audioLevelEvent, System.identityHashCode(this));
		if (audioLevelEvent == null) {
			log.info("No Audio-Levels present");
		}

		setForeground(Color.RED);
		g.fillRect(0, 0, getWidth(), getHeight() / 2);
	}

	@PostConstruct
	public void configure() {
		EventQueue.invokeLater(() -> {
			log.info("Configuring");
			setPreferredSize(new Dimension(48, Integer.MAX_VALUE));
		});
	}
}
