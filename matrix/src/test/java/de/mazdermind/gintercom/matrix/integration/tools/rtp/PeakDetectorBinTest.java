package de.mazdermind.gintercom.matrix.integration.tools.rtp;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class PeakDetectorBinTest {
	@Test
	public void findsPeak() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(ImmutableList.of(1f, 2f, 3f, 2f, 1f));
		assertThat(peaks, contains(2));
	}

	@Test
	public void findsTwoPeaks() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(ImmutableList.of(1f, 2f, 3f, 2f, 3f, 2f, 1f));
		assertThat(peaks, contains(2, 4));
	}

	@Test
	public void ignoresPeakAtStart() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(ImmutableList.of(5f, 4f, 3f, 2f, 1f));
		MatcherAssert.assertThat(peaks, emptyIterable());
	}

	@Test
	public void ignoresPeaksAtEnd() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(ImmutableList.of(0f, 1f, 2f, 3f, 4f, 5f));
		MatcherAssert.assertThat(peaks, emptyIterable());
	}

	@Test
	public void ignoresPeaksAtStartAndEnd() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(ImmutableList.of(5f, 4f, 3f, 2f, 1f, 5f));
		MatcherAssert.assertThat(peaks, emptyIterable());
	}

	@Test
	public void findsPeakWithinNegativeValues() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(ImmutableList.of(-3f, 0f, 3f, 0f, -3f, 1f, 0f));
		MatcherAssert.assertThat(peaks, contains(2, 5));
	}

	@Test
	public void ignoresPlateau() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(ImmutableList.of(0f, 1f, 2f, 2f, 2f, 2f, 1f, 0f));
		MatcherAssert.assertThat(peaks, emptyIterable());
	}

	@Test
	public void handleSingleMeasurement() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(ImmutableList.of(42f));
		MatcherAssert.assertThat(peaks, emptyIterable());
	}

	@Test
	public void findsPeaksInNegativeNumbers() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(ImmutableList.of(-56f, -55f, -54f, -55f, -56f, -54f, -55f));
		MatcherAssert.assertThat(peaks, contains(2, 5));
	}

	@Test
	public void handleEqualMeasurements() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(ImmutableList.of(23f, 23f, 23f, 23f, 23f));
		MatcherAssert.assertThat(peaks, emptyIterable());
	}

	@Test
	public void handlesEmptyList() {
		List<Integer> peaks = PeakDetectorBin.findLocalPeaks(Collections.emptyList());
		MatcherAssert.assertThat(peaks, emptyIterable());
	}
}
