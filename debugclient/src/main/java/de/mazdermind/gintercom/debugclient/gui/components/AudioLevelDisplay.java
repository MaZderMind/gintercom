package de.mazdermind.gintercom.debugclient.gui.components;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.swing.*;

import com.google.common.annotations.VisibleForTesting;

import de.mazdermind.gintercom.debugclient.pipeline.audiolevel.AudioLevelEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioLevelDisplay extends JPanel {
	private static final int SPACING = 1;
	private final AtomicReference<AudioLevelEvent> lastAudioLevelEvent = new AtomicReference<>();

	public AudioLevelDisplay() {
		super();
		setOpaque(true);
		setLayout(null);
	}

	/**
	 * Normalized a Decibel-Value to 0…1
	 * <p>
	 * # -60db -> 0.00 (very quiet)
	 * # -30db -> 0.25
	 * # -15db -> 0.50
	 * #  -5db -> 0.75
	 * #  -0db -> 1.00 (very loud)
	 */
	@VisibleForTesting
	static double normalizeDb(double db) {
		double logscale = 1 - Math.log10(-0.15 * db + 1);
		return clamp(logscale);
	}

	/**
	 * Clamps a Double-Value to 0…1
	 */
	@VisibleForTesting
	static double clamp(double value) {
		return Math.max(Math.min(value, 1.0), 0.0);
	}

	@VisibleForTesting
	static java.util.List<Integer> scaleDecibelLevelToHeight(int height, java.util.List<Double> in) {
		return in.stream()
			.map(peak -> (int) Math.round(normalizeDb(peak) * height))
			.collect(Collectors.toList());
	}

	/**
	 * Calculates width of one of the Volume-Bars based on a given number of Channels
	 * # 1 channel  -> width/1 - 0 SPACINGs
	 * # 2 channels -> width/2 - 1 SPACING
	 * # 3 channels -> width/3 - 2 SPACINGs
	 */
	@VisibleForTesting
	static int calculateChannelWidth(int width, int numChannels) {
		double spaceTakenUpByGutter = SPACING * (numChannels - 1);
		double availableWidth = (double) width - spaceTakenUpByGutter;
		double channelWidth = availableWidth / numChannels;
		return (int) Math.round(channelWidth);
	}

	/**
	 * Calculates the horizontal offset of a Volume-Bar
	 */
	@VisibleForTesting
	static int calculateChannelHorizontalOffset(int width, int numChannels, int channelIndex) {
		double spaceTakenUpByGutter = SPACING * (numChannels - 1);
		double availableWidth = (double) width - spaceTakenUpByGutter;
		double channelWidth = availableWidth / numChannels;
		double channelOffset = (channelWidth + SPACING) * channelIndex;
		return (int) Math.round(channelOffset);
	}

	public void updateLevel(AudioLevelEvent audioLevel) {
		log.trace("Received Audio-Level Event");
		lastAudioLevelEvent.set(audioLevel);
		EventQueue.invokeLater(() -> {
			log.trace("Scheduled repaint");
			repaint();
		});
	}

	public void clear() {
		log.trace("Clearing Audio-Level Display");
		lastAudioLevelEvent.set(null);
		EventQueue.invokeLater(() -> {
			log.trace("Scheduled repaint");
			repaint();
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		log.trace("repaint");

		final int height = getHeight();
		final int width = getWidth();

		AudioLevelEvent audioLevel = lastAudioLevelEvent.get();
		if (audioLevel == null) {
			g.clearRect(0, 0, width, height);
			log.info("No Audio-Levels present");
			return;
		}

		List<Integer> rmsPx = scaleDecibelLevelToHeight(height, audioLevel.getRms());
		List<Integer> peakPx = scaleDecibelLevelToHeight(height, audioLevel.getPeak());
		List<Integer> decayPx = scaleDecibelLevelToHeight(height, audioLevel.getDecay());

		int channelWidth = calculateChannelWidth(width, audioLevel.getChannelCount());
		for (int channelIndex = 0; channelIndex < audioLevel.getChannelCount(); channelIndex++) {
			int horizontalOffset = calculateChannelHorizontalOffset(width, audioLevel.getChannelCount(), channelIndex);

			// Background
			g.setColor(Color.GRAY);
			g.fillRect(horizontalOffset, 0, channelWidth, height);

			// Peak Bar
			g.setColor(Color.YELLOW);
			g.fillRect(horizontalOffset, height - peakPx.get(channelIndex), channelWidth, peakPx.get(channelIndex));

			// RMS Bar
			g.setColor(Color.GREEN);
			g.fillRect(horizontalOffset, height - rmsPx.get(channelIndex), channelWidth, rmsPx.get(channelIndex));

			// Decay Line
			g.setColor(Color.RED);
			g.fillRect(horizontalOffset, height - decayPx.get(channelIndex), channelWidth, 2);
		}
	}
}
