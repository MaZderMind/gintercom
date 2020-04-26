package de.mazdermind.gintercom.mixingcore.it.tools.peakdetector;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PeakSet {
	public static final Set<Double> SILENCE = Collections.emptySet();

	private final List<Peak> peaks;

	public PeakSet(List<Peak> peaks) {
		this.peaks = peaks;
	}

	public List<Peak> getPeaks() {
		return Collections.unmodifiableList(peaks);
	}

	public boolean matches(Set<Double> expectedFrequencies) {
		boolean allExpectedFrequenciesFound = expectedFrequencies.stream().allMatch(frequency ->
				peaks.stream().anyMatch(peak -> peak.contains(frequency)));

		boolean allPeaksExpected = peaks.stream().allMatch(peak ->
				expectedFrequencies.stream().anyMatch(peak::contains));

		return allExpectedFrequenciesFound && allPeaksExpected;
	}

	public void assertMatches(Set<Double> expectedFrequencies) {
		if (!matches(expectedFrequencies)) {
			throw new FrequencyMismatchException(expectedFrequencies, peaks);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("peaks", peaks)
				.toString();
	}
}
