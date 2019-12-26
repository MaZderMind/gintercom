package de.mazdermind.gintercom.matrix.integration.tools.peakdetector;

import java.util.List;
import java.util.Set;

public class FrequencyMismatchException extends RuntimeException {
	public FrequencyMismatchException(Set<Double> expectedFrequencies, List<Peak> foundPeaks) {
		super(String.format("Expected Peaks at %s but found Peaks at %s", expectedFrequencies, foundPeaks));
	}
}
