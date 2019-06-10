package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import static java.util.Collections.emptyList;

import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class PeakDetectorBinIT {

	private Pipeline pipeline;
	private PeakDetectorBin.ElementMessageHandler messageHandler;

	@Before
	public void initGstreamer() {
		Gst.init();
	}

	@After
	public void teardownTestPipeline() {
		pipeline.getBus().disconnect(messageHandler);
		pipeline.stop();
	}

	@Test
	public void detectsSilence() {
		PeakDetectorBin peakDetector = setupTestPipeline("audiotestsrc wave=silence volume=0.2 ! audiomixer name=mix");
		peakDetector.awaitPeaks(emptyList());
	}

	@Test(expected = AssertionError.class)
	public void discriminatesSilenceFromSingleTone() {
		PeakDetectorBin peakDetector = setupTestPipeline("audiotestsrc wave=sine freq=440 volume=0.2 ! audiomixer name=mix");
		peakDetector.awaitPeaks(emptyList());
	}

	@Test
	public void detectsSingleTone() {
		PeakDetectorBin peakDetector = setupTestPipeline("audiotestsrc wave=sine freq=660 volume=0.2 ! audiomixer name=mix");
		peakDetector.awaitPeaks(ImmutableList.of(660));
	}

	@Test(expected = AssertionError.class)
	public void discriminatesSingleToneFromTwoTones() {
		PeakDetectorBin peakDetector = setupTestPipeline(
			"audiomixer name=mix " +
				"audiotestsrc wave=sine freq=660 volume=0.2 ! mix. " +
				"audiotestsrc wave=sine freq=880 volume=0.2 ! mix."
		);
		peakDetector.awaitPeaks(ImmutableList.of(880));
	}

	@Test
	public void detectsTwoTones() {
		PeakDetectorBin peakDetector = setupTestPipeline(
			"audiomixer name=mix " +
				"audiotestsrc wave=sine freq=1020 volume=0.2 ! mix. " +
				"audiotestsrc wave=sine freq=880 volume=0.2 ! mix."
		);
		peakDetector.awaitPeaks(ImmutableList.of(880, 1020));

	}

	@Test(expected = AssertionError.class)
	public void discriminatesTwoTonesFromThreeTones() {
		PeakDetectorBin peakDetector = setupTestPipeline(
			"audiomixer name=mix " +
				"audiotestsrc wave=sine freq=1020 volume=0.2 ! mix. " +
				"audiotestsrc wave=sine freq=880 volume=0.2 ! mix. " +
				"audiotestsrc wave=sine freq=3000 volume=0.2 ! mix."
		);
		peakDetector.awaitPeaks(ImmutableList.of(880, 1020));
	}

	@Test
	public void detectsThreeTones() {
		PeakDetectorBin peakDetector = setupTestPipeline(
			"audiomixer name=mix " +
				"audiotestsrc wave=sine freq=3000 volume=0.2 ! mix. " +
				"audiotestsrc wave=sine freq=220 volume=0.2 ! mix. " +
				"audiotestsrc wave=sine freq=880 volume=0.2 ! mix."
		);
		peakDetector.awaitPeaks(ImmutableList.of(220, 880, 3000));
	}

	private PeakDetectorBin setupTestPipeline(String pipelineDescription) {
		PeakDetectorBin peakDetector = new PeakDetectorBin();
		pipeline = (Pipeline) Gst.parseLaunch(pipelineDescription);
		pipeline.add(peakDetector);
		messageHandler = new PeakDetectorBin.ElementMessageHandler(peakDetector);
		pipeline.getBus().connect(messageHandler);
		pipeline.getElementByName("mix").link(peakDetector);

		pipeline.play();

		return peakDetector;
	}

	@Test
	public void detectsSingleTonesBetween200hzAnd10KHz() {
		for (int freq = 200; freq <= 10_000; freq += 100) {
			PeakDetectorBin peakDetector = setupTestPipeline(
				String.format("audiotestsrc wave=sine freq=%d volume=0.2 ! audiomixer name=mix", freq));
			peakDetector.awaitPeaks(ImmutableList.of(freq));
			pipeline.getBus().disconnect(messageHandler);
			pipeline.stop();
		}
	}
}
