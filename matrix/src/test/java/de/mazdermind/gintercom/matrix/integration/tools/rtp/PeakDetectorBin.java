package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import static java.util.Collections.unmodifiableList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.GhostPad;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.message.Message;
import org.freedesktop.gstreamer.message.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mazdermind.gintercom.shared.pipeline.support.ElementFactory;

public class PeakDetectorBin extends Bin {
	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);
	private static final int NUMBER_OF_BANDS = 1020; // Should be a multiple of 30 (which equals being dividable by b 2, 3, and 5) for best performance
	private static final float THRESHOLD = -60.0f;

	private static Logger log = LoggerFactory.getLogger(PeakDetectorBin.class);
	private final AtomicReference<AwaitedPeaks> awaitedPeaks = new AtomicReference<>();
	private Element spectrum;

	public PeakDetectorBin() {
		super("PeakDetectorBin");

		ElementFactory factory = new ElementFactory(this);
		spectrum = factory.createAndAddElement("spectrum");
		spectrum.set("bands", NUMBER_OF_BANDS);
		spectrum.set("threshold", THRESHOLD);

		Element sink = factory.createAndAddElement("fakesink");
		spectrum.link(sink);

		Pad srcPad = spectrum.getStaticPad("sink");
		GhostPad ghostPad = new GhostPad("sink", srcPad);
		addPad(ghostPad);
	}

	static List<Integer> findLocalPeaks(List<Float> magnitudes) {
		return findLocalPeaks(magnitudes, Float.NEGATIVE_INFINITY);
	}

	static List<Integer> findLocalPeaks(List<Float> magnitudes, float threshold) {
		List<Integer> peaks = new ArrayList<>();

		int size = magnitudes.size();
		for (int i = 1; i < size - 1; i++) {
			Float last = magnitudes.get(i - 1);
			Float cur = magnitudes.get(i);
			Float next = magnitudes.get(i + 1);

			if (last < cur && cur > next && cur > threshold) {
				peaks.add(i);
			}
		}

		return peaks;
	}

	public void expectPeaks(List<Integer> peakFrequencies) {
		expectPeaks(peakFrequencies, DEFAULT_TIMEOUT);
	}

	public void expectPeaks(List<Integer> peakFrequencies, Duration timeout) {
		AwaitedPeaks awaitedPeaks = new AwaitedPeaks(peakFrequencies);
		this.awaitedPeaks.set(awaitedPeaks);
		try {
			Boolean success = awaitedPeaks.getFuture().get(timeout.toMillis(), TimeUnit.MILLISECONDS);
			if (success) {
				return;
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			//
		}

		throw new AssertionError(String.format("Awaited Peaks %s not detected in Timeout %s", peakFrequencies, timeout));
	}

	private void spectrumMessageCallback(List<Float> foundMagnitudes) {
		AwaitedPeaks awaitedPeaks = this.awaitedPeaks.get();
		if (awaitedPeaks != null && !awaitedPeaks.getFuture().isDone()) {
			int remainingTries = awaitedPeaks.decrementMismatches();
			if (remainingTries == 0) {
				log.warn("too many mismatches, resigning");
				awaitedPeaks.getFuture().complete(false);
			}
			log.debug("remaining tries: {}", remainingTries);

			List<Integer> foundPeaks = findLocalPeaks(foundMagnitudes, THRESHOLD + 10.0f);
			log.debug("found peaks in the following bands: {}", foundPeaks);

			Float bandWidth = calculateBandWidth();
			if (bandWidth == null) {
				log.info("pads not ready yet");
				return;
			}
			log.debug("calculated the frequency width of one band to be {} Hz", bandWidth);

			List<FrequencySpan> foundFrequencyBands = foundPeaks.stream()
				.map(band -> new FrequencySpan((band - 1) * bandWidth, (band + 1) * bandWidth))
				.collect(Collectors.toList());

			log.debug("calculated expected bands to be {}", foundFrequencyBands);

			Iterator<Integer> awaitedPeaksIterator = awaitedPeaks.getAwaitedPeaks().iterator();
			boolean matchesExpectedPeaks = foundFrequencyBands.stream().allMatch(band -> {
				if (!awaitedPeaksIterator.hasNext()) {
					log.warn("found unexpected Peak in Band {}", band);
					return false;
				}

				Integer awaitedPeak = awaitedPeaksIterator.next();
				if (band.contains(awaitedPeak)) {
					log.debug("found expected peak at {} in band {}", awaitedPeak, band);
					return true;
				} else {
					log.warn("expected Peak {} but instead found Peak in Band {}", awaitedPeak, band);
					return false;
				}
			});

			if (matchesExpectedPeaks) {
				log.debug("all expected peaks were found");
				awaitedPeaks.getFuture().complete(true);
			}
		}
	}

	private Float calculateBandWidth() {
		Caps caps = spectrum.getStaticPad("sink").getCurrentCaps();
		if (caps == null) {
			return null;
		}
		int rate = caps.getStructure(0).getInteger("rate");

		return (float) rate / 2 / NUMBER_OF_BANDS;

	}

	private static class FrequencySpan {
		private float lower;
		private float upper;

		public FrequencySpan(float lower, float upper) {
			this.lower = lower;
			this.upper = upper;
		}

		public boolean contains(float freqency) {
			return lower < freqency && freqency < upper;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
				.append("lower", lower)
				.append("upper", upper)
				.toString();
		}
	}

	private static class AwaitedPeaks {
		private final List<Integer> awaitedPeaks;
		private final CompletableFuture<Boolean> future = new CompletableFuture<>();
		private int mismatches = 5;

		public AwaitedPeaks(List<Integer> awaitedPeaks) {
			this.awaitedPeaks = awaitedPeaks;
		}

		public List<Integer> getAwaitedPeaks() {
			return unmodifiableList(awaitedPeaks);
		}

		public CompletableFuture<Boolean> getFuture() {
			return future;
		}

		public int decrementMismatches() {
			return --mismatches;
		}
	}

	public static class ElementMessageHandler implements Bus.MESSAGE {
		private final PeakDetectorBin peakDetectorBin;

		public ElementMessageHandler(PeakDetectorBin peakDetectorBin) {
			this.peakDetectorBin = peakDetectorBin;
		}

		@Override
		public void busMessage(Bus bus, Message message) {
			if (message.getType() == MessageType.ELEMENT && message.getSource().equals(peakDetectorBin.spectrum)) {
				List<Float> magnitudes = message.getStructure().getValues(Float.class, "magnitude");

				try {
					peakDetectorBin.spectrumMessageCallback(magnitudes);
				} catch (Throwable t) {
					log.error("Caught error [}", t);
				}
			}
		}
	}
}
