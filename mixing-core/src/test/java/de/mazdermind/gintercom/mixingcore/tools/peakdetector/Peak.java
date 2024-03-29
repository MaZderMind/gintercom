package de.mazdermind.gintercom.mixingcore.tools.peakdetector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Peak {
	private final double lower;
	private final double upper;

	Peak(int binIndex, double binWidth) {
		this((binIndex - 1) * binWidth, (binIndex + 1) * binWidth);
	}

	Peak(double lower, double upper) {
		this.lower = lower;
		this.upper = upper;
	}

	public boolean contains(double frequency) {
		return lower <= frequency && frequency < upper;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
				.append("lower", lower)
				.append("upper", upper)
				.toString();
	}
}
