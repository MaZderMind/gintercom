package de.mazdermind.gintercom.mixingcore.it.tools.peakdetector;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.FlowReturn;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.elements.AppSink;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class PeakDetectorFunctionalIT {
	private static final int ITERATIONS = 5;
	private static final Duration TIMEOUT = Duration.ofMillis(500);

	static {
		Gst.init();
	}

	private Pipeline pipeline;
	private PeakDetector peakDetector;

	private Element src1;
	private Element src2;
	private Element src3;

	@Before
	public void init() {
		peakDetector = new PeakDetector(48000, PeakDetector.DEFAULT_FFT_BANDS, null);
		pipeline = (Pipeline) Gst.parseLaunch("" +
			"audiomixer name=mix ! " +
			"  audio/x-raw,format=S16LE,channels=1,rate=48000 ! " +
			"  appsink name=sink " +
			"" +
			"audiotestsrc is-live=true wave=silence name=src1 volume=0.2 ! audio/x-raw,format=S16LE,channels=1,rate=48000 ! mix. " +
			"audiotestsrc is-live=true wave=silence name=src2 volume=0.2 ! audio/x-raw,format=S16LE,channels=1,rate=48000 ! mix. " +
			"audiotestsrc is-live=true wave=silence name=src3 volume=0.2 ! audio/x-raw,format=S16LE,channels=1,rate=48000 ! mix. "
		);

		src1 = pipeline.getElementByName("src1");
		src2 = pipeline.getElementByName("src2");
		src3 = pipeline.getElementByName("src3");

		AppSink sink = (AppSink) pipeline.getElementByName("sink");
		sink.set("emit-signals", true);
		sink.connect((AppSink.NEW_SAMPLE) appSink -> {
			long[] samples = AppSinkSupport.extractSampleValues(appSink.pullSample());
			peakDetector.appendSamples(samples);

			return FlowReturn.OK;
		});
	}

	@After
	public void teardownTestPipeline() {
		pipeline.stop();
	}

	@Test
	public void detectsSilence() throws InterruptedException {
		pipeline.play();

		for (int i = 0; i < ITERATIONS; i++) {
			PeakSet peaks = Objects.requireNonNull(peakDetector.getPeakSearchResults().poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
			peaks.assertMatches(PeakSet.SILENCE);
		}
	}

	@Test
	public void discriminatesSilenceFromSingleTone() throws InterruptedException {
		src1.set("wave", 0);
		src1.set("freq", 800.);

		pipeline.play();

		for (int i = 0; i < ITERATIONS; i++) {
			PeakSet peaks = Objects.requireNonNull(peakDetector.getPeakSearchResults().poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));

			boolean matches = peaks.matches(PeakSet.SILENCE);
			assertThat(matches).isFalse();
		}
	}


	@Test
	public void discriminatesSingleToneFromSilence() throws InterruptedException {
		pipeline.play();

		for (int i = 0; i < ITERATIONS; i++) {
			PeakSet peaks = Objects.requireNonNull(peakDetector.getPeakSearchResults().poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));

			boolean matches = peaks.matches(ImmutableSet.of(1000.));
			assertThat(matches).isFalse();
		}
	}

	@Test
	public void detectsSingleTone() throws InterruptedException {
		src1.set("wave", 0);
		src1.set("freq", 300.);

		pipeline.play();

		for (int i = 0; i < ITERATIONS; i++) {
			PeakSet peaks = Objects.requireNonNull(peakDetector.getPeakSearchResults().poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
			peaks.assertMatches(ImmutableSet.of(300.));
		}
	}

	@Test
	public void discriminatesSingleToneFromTwoTones() throws InterruptedException {
		src1.set("wave", 0);
		src1.set("freq", 800.);

		src2.set("wave", 0);
		src2.set("freq", 400.);

		pipeline.play();

		for (int i = 0; i < ITERATIONS; i++) {
			PeakSet peaks = Objects.requireNonNull(peakDetector.getPeakSearchResults().poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));

			boolean matches = peaks.matches(ImmutableSet.of(400.));
			assertThat(matches).isFalse();
		}
	}

	@Test
	public void detectsTwoTones() throws InterruptedException {
		src1.set("wave", 0);
		src1.set("freq", 800.);

		src2.set("wave", 0);
		src2.set("freq", 400.);

		pipeline.play();

		for (int i = 0; i < ITERATIONS; i++) {
			PeakSet peaks = Objects.requireNonNull(peakDetector.getPeakSearchResults().poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));

			peaks.assertMatches(ImmutableSet.of(400., 800.));
		}
	}

	@Test
	public void discriminatesTwoTonesFromThreeTones() throws InterruptedException {
		src1.set("wave", 0);
		src1.set("freq", 800.);

		src2.set("wave", 0);
		src2.set("freq", 400.);

		src3.set("wave", 0);
		src3.set("freq", 1000.);

		pipeline.play();

		for (int i = 0; i < ITERATIONS; i++) {
			PeakSet peaks = Objects.requireNonNull(peakDetector.getPeakSearchResults().poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));

			boolean matches = peaks.matches(ImmutableSet.of(400., 800.));
			assertThat(matches).isFalse();
		}
	}

	@Test
	public void detectsThreeTones() throws InterruptedException {
		src1.set("wave", 0);
		src1.set("freq", 800.);

		src2.set("wave", 0);
		src2.set("freq", 400.);

		src3.set("wave", 0);
		src3.set("freq", 1000.);

		pipeline.play();

		for (int i = 0; i < ITERATIONS; i++) {
			PeakSet peaks = Objects.requireNonNull(peakDetector.getPeakSearchResults().poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));

			peaks.assertMatches(ImmutableSet.of(400., 800., 1000.));
		}
	}

	/**
	 * Lower Bound is around 100 Hz, Upper bound is aat Fs / 8 (=48000 / 8 = 6000 Hz)
	 */
	@Test
	public void detectsSingleTonesBetween100hzAnd6KHz() throws InterruptedException {
		src1.set("wave", 0);

		for (int freq = 100; freq < 6000; freq += 100) {
			src1.set("freq", freq);
			pipeline.play();

			PeakSet peaks = Objects.requireNonNull(peakDetector.getPeakSearchResults().poll(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS));
			peaks.assertMatches(ImmutableSet.of((double) freq));

			pipeline.stop();
			pipeline.seek(0);
			peakDetector.clear();
		}
	}
}
