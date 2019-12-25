package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.freedesktop.gstreamer.Buffer;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.PadProbeReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoDataDetector implements Pad.DATA_PROBE {
	public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
	public static final Duration DEFAULT_PERIOD = Duration.ofMillis(500);

	private static final int TIMER_PERIOD_MS = 100;
	private static final Logger log = LoggerFactory.getLogger(NoDataDetector.class);

	private AtomicReference<LocalDateTime> lastDataReceived = new AtomicReference<>();

	/**
	 * Waits up to {@link #DEFAULT_TIMEOUT} for no a Period of {@link #DEFAULT_PERIOD} length, where no data
	 * has been received.
	 */
	public void awaitNoMoreData() {
		awaitNoMoreData(DEFAULT_TIMEOUT, DEFAULT_PERIOD);
	}

	public void awaitNoMoreData(Duration timeout, Duration period) {
		CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
		Timer timer = new Timer("NoDataDetector");

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				LocalDateTime lastData = lastDataReceived.get();
				if (lastData != null && lastData.plus(period).isBefore(LocalDateTime.now())) {
					completableFuture.complete(true);
				}
			}
		}, TIMER_PERIOD_MS, TIMER_PERIOD_MS);

		try {
			completableFuture.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			if (lastDataReceived.get() != null) {
				throw new AssertionError("No Period of the expected Duration with no Data has been found during the given Timeout.");
			}
		} finally {
			timer.cancel();
		}
	}

	@Override
	public PadProbeReturn dataReceived(Pad pad, Buffer buffer) {
		log.trace("dataReceived");
		lastDataReceived.set(LocalDateTime.now());
		return PadProbeReturn.OK;
	}
}
