package de.mazdermind.gintercom.shared.pipeline.support;

import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.GstObject;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class PipelineStateChangeListener implements Bus.EOS, Bus.STATE_CHANGED {
	private static final Logger log = LoggerFactory.getLogger(PipelineStateChangeListener.class);

	@Override
	public void endOfStream(GstObject source) {
		log.error("EOS received");
		throw new PipelineException();
	}

	@Override
	public void stateChanged(GstObject source, State old, State current, State pending) {
		if (source instanceof Pipeline) {
			log.info("State changed from {} to {} pending {}", old, current, pending);
		} else {
			log.trace("State changed from {} to {} pending {}", old, current, pending);
		}
	}
}
