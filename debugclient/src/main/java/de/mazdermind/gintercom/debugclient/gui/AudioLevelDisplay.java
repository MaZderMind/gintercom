package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.debugclient.pipeline.audiolevel.AudioLevelEvent;

@Component
@Lazy
public class AudioLevelDisplay extends JPanel {
	private static Logger log = LoggerFactory.getLogger(AudioLevelDisplay.class);
	private final AtomicReference<AudioLevelEvent> lastAudioLevelEvent = new AtomicReference<>();

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
			setPreferredSize(new Dimension(48, Integer.MAX_VALUE));
		});
	}

	@EventListener
	public void onAudioLevelEvent(@NonNull AudioLevelEvent audioLevelEvent) {
		this.lastAudioLevelEvent.set(audioLevelEvent);
		EventQueue.invokeLater(() -> {
			this.invalidate();
			this.repaint();
		});
	}
}
