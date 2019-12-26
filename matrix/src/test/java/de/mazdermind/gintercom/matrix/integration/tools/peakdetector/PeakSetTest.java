package de.mazdermind.gintercom.matrix.integration.tools.peakdetector;

import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class PeakSetTest {
	@Test
	public void acceptNoPeaks() {
		PeakSet peakSet = new PeakSet(Collections.emptyList());
		peakSet.assertMatches(Collections.emptySet());
	}

	@Test
	public void acceptMatchingPeaks() {
		PeakSet peakSet = new PeakSet(ImmutableList.of(new Peak(10., 20.)));
		peakSet.assertMatches(ImmutableSet.of(15.));
	}

	@Test(expected = FrequencyMismatchException.class)
	public void rejectsMissingPeak() {
		PeakSet peakSet = new PeakSet(ImmutableList.of(new Peak(10., 20.)));
		peakSet.assertMatches(ImmutableSet.of(15., 100.));
	}

	@Test(expected = FrequencyMismatchException.class)
	public void rejectsUnexpectedPeak() {
		PeakSet peakSet = new PeakSet(ImmutableList.of(new Peak(10., 20.), new Peak(100., 110.)));
		peakSet.assertMatches(ImmutableSet.of(15.));
	}
}
