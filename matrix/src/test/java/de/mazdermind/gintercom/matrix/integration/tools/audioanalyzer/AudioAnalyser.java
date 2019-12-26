package de.mazdermind.gintercom.matrix.integration.tools.audioanalyzer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mazdermind.gintercom.matrix.integration.tools.peakdetector.PeakDetector;
import de.mazdermind.gintercom.matrix.integration.tools.peakdetector.PeakSet;

public class AudioAnalyser {
	public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
	public static final Duration DEFAULT_PERIOD = Duration.ofMillis(250);
	public static final Duration POLL_TIMEOUT = Duration.ofMillis(500);
	private static final Logger log = LoggerFactory.getLogger(AudioAnalyser.class);
	private final PeakDetector peakDetector;

	public AudioAnalyser(int sampleRate) {
		peakDetector = new PeakDetector(sampleRate);
	}

	public void awaitFrequencies(Duration period, Duration timeout, Set<Double> expectedFrequencies) {
		peakDetector.clear();

		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plus(timeout);
		LocalDateTime periodStart = start;

		PeakSet peaks = null;
		while (LocalDateTime.now().isBefore(end)) {
			try {
				peaks = peakDetector.getPeakSearchResults().poll(POLL_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				throw new AudioAnalyserException("Interrupted", e);
			}

			if (peaks == null) {
				throw new AudioAnalyserException(String.format(
					"During the POLL_TIMEOUT of %s not enough data for an FFT was received", POLL_TIMEOUT));
			}

			if (!peaks.matches(expectedFrequencies)) {
				periodStart = LocalDateTime.now();
				continue;
			}

			if (LocalDateTime.now().isAfter(periodStart.plus(period))) {
				log.info("found expected frequencies {} over a period of {}ms",
					expectedFrequencies, ChronoUnit.MILLIS.between(periodStart, LocalDateTime.now()));

				return;
			}
		}

		throw new AudioAnalyserException(String.format(
			"The expectedFrequencies were not found before reaching the TIMEOUT %s. " +
				"The last found frequencies were %s", timeout, peaks));
	}

	public void awaitData(Duration timeout) {
		peakDetector.clear();

		LocalDateTime start = LocalDateTime.now();
		PeakSet peaks;
		try {
			peaks = peakDetector.getPeakSearchResults().poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new AudioAnalyserException("Interrupted", e);
		}

		if (peaks == null) {
			throw new AudioAnalyserException(String.format(
				"During the timeout of %s not enough data was received", timeout));
		}

		log.info("received data within a timeout of {}ms",
			ChronoUnit.MILLIS.between(start, LocalDateTime.now()));
	}

	public void awaitNoData(Duration period, Duration timeout) {
		peakDetector.clear();

		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plus(timeout);
		LocalDateTime periodStart = start;

		PeakSet peaks = null;
		while (LocalDateTime.now().isBefore(end)) {
			try {
				peaks = peakDetector.getPeakSearchResults().poll(POLL_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				throw new AudioAnalyserException("Interrupted", e);
			}

			if (peaks != null) {
				periodStart = LocalDateTime.now();
				continue;
			}

			if (LocalDateTime.now().isAfter(periodStart.plus(period))) {
				log.info("received no data over a period of {}ms",
					ChronoUnit.MILLIS.between(periodStart, LocalDateTime.now()));

				return;
			}
		}

		throw new AudioAnalyserException(String.format(
			"No period of %s length without data was found before reaching the TIMEOUT %s. " +
				"The last found frequencies were %s", period, timeout, peaks));
	}

	public void awaitSilence(Duration period, Duration timeout) {
		awaitFrequencies(period, timeout, Collections.emptySet());
	}

	public void awaitSilence() {
		awaitSilence(DEFAULT_PERIOD, DEFAULT_TIMEOUT);
	}

	public void awaitData() {
		awaitData(DEFAULT_TIMEOUT);
	}

	public void awaitNoData() {
		awaitNoData(DEFAULT_PERIOD, DEFAULT_TIMEOUT);
	}

	public void awaitFrequencies(Set<Double> expectedFrequencies) {
		awaitFrequencies(DEFAULT_PERIOD, DEFAULT_TIMEOUT, expectedFrequencies);
	}

	public void appendSamples(long[] samples) {
		peakDetector.appendSamples(samples);
	}
}
