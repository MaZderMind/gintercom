package de.mazdermind.gintercom.matrix.pipeline;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.Config;

@Component
@Scope("prototype")
public class PanelReceivePath {
	private static final Logger log = LoggerFactory.getLogger(PanelReceivePath.class);

	private final Config config;

	private Bin bin;
	private Pipeline pipeline;
	private String panelId;

	public PanelReceivePath(
		@Autowired Config config
	) {
		this.config = config;
	}

	public void configure(Pipeline pipeline, String panelId, int rxPort) {
		// FIXME
	}

	public void deconfigure() {
		// FIXME
	}
}
