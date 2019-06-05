package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import static de.mazdermind.gintercom.shared.pipeline.support.GstErrorCheck.expectAsyncOrSuccess;
import static de.mazdermind.gintercom.shared.pipeline.support.GstErrorCheck.expectSuccess;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Buffer;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.PadProbeReturn;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Scope;

import de.mazdermind.gintercom.shared.pipeline.StaticCaps;
import de.mazdermind.gintercom.shared.pipeline.support.ElementFactory;

@TestComponent
@Scope("prototype")
public class RtpTestClientRx {
	private static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(1000);
	private static final Duration DEFAULT_WINDOW = Duration.ofMillis(100);

	private static Logger log = LoggerFactory.getLogger(RtpTestClientRx.class);

	private final DataProbe dataProbe = new DataProbe();
	private final AtomicReference<CompletableFuture<Void>> awaitDataFuture = new AtomicReference<>(new CompletableFuture<>());

	private Pipeline pipeline;
	private Pad sinkPad;

	public void connect(Integer matrixToPanelPort) {
		pipeline = new Pipeline();
		ElementFactory factory = new ElementFactory(pipeline);

		Element udpsrc = factory.createAndAddElement("udpsrc");
		udpsrc.set("port", matrixToPanelPort);

		Element jitterbuffer = factory.createAndAddElement("rtpjitterbuffer");
		jitterbuffer.set("latency", 50);
		Element.linkPadsFiltered(udpsrc, "src", jitterbuffer, "sink", StaticCaps.RTP);

		Element depay = factory.createAndAddElement("rtpL16depay");
		jitterbuffer.link(depay);

		Element sink = factory.createAndAddElement("fakesink");
		sinkPad = sink.getStaticPad("sink");
		sinkPad.addDataProbe(dataProbe);
		depay.link(sink);

		log.info("Starting Test-RTP-Rx-Client on Port {}", matrixToPanelPort);
		expectAsyncOrSuccess(pipeline.play());
		pipeline.debugToDotFileWithTS(Bin.DebugGraphDetails.SHOW_ALL, "ttp-test-client-rx");
	}

	public void awaitAudioData() {
		awaitAudioData(DEFAULT_TIMEOUT);
	}

	public void awaitAudioData(Duration timeout) {
		CompletableFuture<Void> future = new CompletableFuture<>();

		awaitDataFuture.set(future);
		try {
			future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
			awaitDataFuture.set(null);
			// Successfull received Audio in the given Timeout
			return;
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			// Timed Out waiting for Audio within Window
		} finally {
			awaitDataFuture.set(null);
		}

		throw new AssertionError(String.format("Expected Audio has not been received within the Timeout %s", timeout));
	}

	public void awaitNoAudioData() {
		awaitNoAudioData(DEFAULT_TIMEOUT, DEFAULT_WINDOW);
	}

	/**
	 * Monitor a Window of window Miliseconds and expect no Audio to be received.
	 * I unexpected Audio was received, repeat until timeout has been reached.
	 */
	public void awaitNoAudioData(Duration timeout, Duration window) {
		LocalTime end = LocalTime.now().plus(timeout);

		while (LocalTime.now().isBefore(end)) {
			try {
				awaitNoAudioData(window);
				// Successfully Timed Out waiting for Audio within Window
				return;
			} catch (AssertionError e) {
				// Received Audio within Window but the Timeout is not yet over -- retry
			}
		}

		// Timeout over and all tested Windows contained Audio
		throw new AssertionError(String.format("Unexpected Audio has been received within the monitored Duration of %s", timeout));
	}

	/**
	 * Monitor a Window of window Miliseconds and expect no Audio to be received.
	 */
	public void awaitNoAudioData(Duration window) {
		CompletableFuture<Void> future = new CompletableFuture<>();

		awaitDataFuture.set(future);
		try {
			future.get(window.toMillis(), TimeUnit.MILLISECONDS);
			// Received Audio within Window
			throw new AssertionError(String.format("Unexpected Audio has been received within the monitored Window of %s", window));
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			// Successfully Timed Out waiting for Audio within Window
		} finally {
			awaitDataFuture.set(null);
		}
	}

	public void cleanup() {
		sinkPad.removeDataProbe(dataProbe);
		expectSuccess(pipeline.stop());
	}

	private class DataProbe implements Pad.DATA_PROBE {
		@Override
		public PadProbeReturn dataReceived(Pad pad, Buffer buffer) {
			CompletableFuture<Void> future = awaitDataFuture.get();
			if (future != null) {
				future.complete(null);
			}
			return PadProbeReturn.OK;
		}
	}
}
