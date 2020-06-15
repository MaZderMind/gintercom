package de.mazdermind.gintercom.debugclient.gui;

import java.awt.*;

import javax.swing.*;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientsupport.events.connectionlifecycle.DisconnectedEvent;
import de.mazdermind.gintercom.debugclient.gui.components.AudioLevelDisplay;
import de.mazdermind.gintercom.debugclient.pipeline.audiolevel.AudioLevelEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AudioLevelDisplayManager {
	private AudioLevelDisplay audioLevelDisplay;

	/**
	 * Muss be called from AWT Thread
	 */
	public JPanel create() {
		log.info("Creating");
		AudioLevelDisplay audioLevelDisplay = new AudioLevelDisplay();

		audioLevelDisplay.setPreferredSize(new Dimension(48, Integer.MAX_VALUE));
		audioLevelDisplay.setVisible(true);

		assert this.audioLevelDisplay == null : "only one AudioLevelDisplay is supported";
		this.audioLevelDisplay = audioLevelDisplay;
		return audioLevelDisplay;
	}

	@EventListener
	public void audioLevelEventHandler(AudioLevelEvent audioLevelEvent) {
		log.trace("Received Audio-Level Event");
		audioLevelDisplay.updateLevel(audioLevelEvent);
	}

	@EventListener
	public void clearOnDisconnect(DisconnectedEvent disconnectedEvent) {
		audioLevelDisplay.clear();
	}
}
