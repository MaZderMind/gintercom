package de.mazdermind.gintercom.debugclient.pipeline.audiolevel;

import java.util.List;

import com.google.common.primitives.Doubles;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AudioLevelEvent {
	private final List<Double> peak;
	private final List<Double> decay;
	private final List<Double> rms;

	public AudioLevelEvent(double[] peak, double[] decay, double[] rms) {
		this.peak = Doubles.asList(peak);
		this.decay = Doubles.asList(decay);
		this.rms = Doubles.asList(rms);
	}

	public int getChannelCount() {
		return peak.size();
	}
}
