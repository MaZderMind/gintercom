package de.mazdermind.gintercom.debugClient.pipeline.audiolevel;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.ApplicationEvent;

import com.google.common.base.Objects;
import com.google.common.primitives.Doubles;

public class AudioLevelEvent extends ApplicationEvent {
	private final double[] peak;
	private final double[] decay;
	private final double[] rms;

	public AudioLevelEvent(Object source, double[] peak, double[] decay, double[] rms) {
		super(source);
		assert peak.length == decay.length && decay.length == rms.length;

		this.peak = peak;
		this.decay = decay;
		this.rms = rms;
	}

	public List<Double> getPeak() {
		return Doubles.asList(peak);
	}

	public List<Double> getDecay() {
		return Doubles.asList(decay);
	}

	public List<Double> getRms() {
		return Doubles.asList(rms);
	}

	public int getChannelCount() {
		return peak.length;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("#channels", getChannelCount())
			.append("peak", peak)
			.append("decay", decay)
			.append("rms", rms)
			.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(peak, decay, rms);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AudioLevelEvent that = (AudioLevelEvent) o;
		return Objects.equal(peak, that.peak) &&
			Objects.equal(decay, that.decay) &&
			Objects.equal(rms, that.rms);
	}
}
