package de.mazdermind.gintercom.matrix.integration.tools.peakdetector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeakDetector {
	public static final int DEFAULT_FFT_BANDS = 512;
	private static final Logger log = LoggerFactory.getLogger(PeakDetector.class);

	private final int fftSize;
	private final double binWidth;

	private final LinkedBlockingQueue<PeakSet> peakSearchResults = new LinkedBlockingQueue<>();

	private final CircularFifoQueue<Long> sampleBuffer;
	private final FastFourierTransformer transformer;

	public PeakDetector(int sampleRate, int fftBands) {
		if (!ArithmeticUtils.isPowerOfTwo(fftBands)) {
			throw new IllegalArgumentException(String.format("fftBands must be a power of 2, %s isn't", fftBands));
		}

		this.fftSize = fftBands * 2;
		this.binWidth = (double) sampleRate / fftSize / 4;

		sampleBuffer = new CircularFifoQueue<>(fftSize * 2);
		transformer = new FastFourierTransformer(DftNormalization.STANDARD);

	}

	public PeakDetector(int sampleRate) {
		this(sampleRate, DEFAULT_FFT_BANDS);
	}

	public void appendSamples(long[] samples) {
		LongStream.of(samples).forEach(sampleBuffer::add);
		log.trace("received {} samples, sampleBuffer now at {}", samples.length, sampleBuffer.size());

		if (sampleBuffer.size() >= fftSize) {
			log.trace("sampleBuffer now over {}, performing fft", fftSize);
			double[] magnitudes = performFft();
			List<Peak> peaks = performPeakSearch(magnitudes);

			peakSearchResults.add(new PeakSet(peaks));
		}
	}

	public void clear() {
		sampleBuffer.clear();
		peakSearchResults.clear();
	}

	public BlockingQueue<PeakSet> getPeakSearchResults() {
		return peakSearchResults;
	}

	private double[] performFft() {
		double[] batch = IntStream.range(0, fftSize).mapToDouble(i -> sampleBuffer.remove()).toArray();
		log.trace("extracted set of {} values for fft, sampleBuffer now at {}", batch.length, sampleBuffer.size());

		Complex[] fft = transformer.transform(batch, TransformType.FORWARD);
		double[] magnitudes = Stream.of(fft).limit(fft.length / 2).mapToDouble(bin -> {
			double rr = bin.getReal();
			double ri = bin.getImaginary();

			return Math.sqrt((rr * rr) + (ri * ri));
		}).toArray();

		log.trace("magnitudes: {}", magnitudes);
		return magnitudes;
	}

	List<Peak> performPeakSearch(double[] magnitudes) {
		List<Integer> binsWithPeaks = new ArrayList<>();

		double maximumMagnitude = DoubleStream.of(magnitudes).max().orElse(0.0);
		double threshold = maximumMagnitude * 0.3;

		int size = magnitudes.length;
		for (int i = 1; i < size - 1; i++) {
			double last = magnitudes[i - 1];
			double cur = magnitudes[i];
			double next = magnitudes[i + 1];

			if (last < cur && cur > next && cur > threshold) {
				binsWithPeaks.add(i);
			}
		}

		List<Peak> frequencyPeaks = binsWithPeaks.stream()
			.map(bin -> new Peak(bin, binWidth))
			.collect(Collectors.toList());

		log.trace("found peaks in bins {} which correspond to frequencies {}", binsWithPeaks, frequencyPeaks);
		return frequencyPeaks;
	}
}
