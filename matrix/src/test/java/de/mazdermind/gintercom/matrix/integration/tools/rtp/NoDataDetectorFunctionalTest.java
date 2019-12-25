package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import java.time.Duration;

import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NoDataDetectorFunctionalTest {
	private static final Duration SHORT_DURATION = Duration.ofMillis(1500);

	private Pipeline pipeline;
	private NoDataDetector noDataDetector;

	@Before
	public void init() {
		Gst.init();

		noDataDetector = new NoDataDetector();
	}

	@After
	public void teardownTestPipeline() {
		pipeline.getElementByName("src")
			.getSrcPads().get(0)
			.removeDataProbe(noDataDetector);

		pipeline.stop();
	}

	@Test(expected = AssertionError.class)
	public void detectsData() {
		pipeline = (Pipeline) Gst.parseLaunch("audiotestsrc is-live=true name=src ! fakesink");
		pipeline.ready();

		pipeline.getElementByName("src")
			.getSrcPads().get(0)
			.addDataProbe(noDataDetector);

		pipeline.play();

		noDataDetector.awaitNoMoreData(SHORT_DURATION, NoDataDetector.DEFAULT_PERIOD);
	}

	@Test
	public void detectsNoDataAtAll() {
		pipeline = (Pipeline) Gst.parseLaunch("audiotestsrc num-buffers=0 is-live=true name=src ! fakesink");
		pipeline.ready();

		pipeline.getElementByName("src")
			.getSrcPads().get(0)
			.addDataProbe(noDataDetector);

		pipeline.play();

		noDataDetector.awaitNoMoreData(SHORT_DURATION, NoDataDetector.DEFAULT_PERIOD);
	}

	@Test
	public void detectsDataDropout() {
		pipeline = (Pipeline) Gst.parseLaunch("audiotestsrc num-buffers=10 is-live=true name=src ! fakesink");
		pipeline.ready();

		pipeline.getElementByName("src")
			.getSrcPads().get(0)
			.addDataProbe(noDataDetector);

		pipeline.play();

		noDataDetector.awaitNoMoreData(SHORT_DURATION, NoDataDetector.DEFAULT_PERIOD);
	}
}
