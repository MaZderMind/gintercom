package de.mazdermind.gintercom.matrix.pipeline;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.Config;

@Component
public class MainPipeline {
	private static final Logger log = LoggerFactory.getLogger(MainPipeline.class);
	private final Config config;
	private Pipeline pipeline;

	public MainPipeline(@Autowired Config config) {
		this.config = config;
	}

	@PostConstruct
	public void start() {
		log.info("initializing Gstreamer");
		Gst.init();

		log.info("creating pipeline");
		String pipeSpec = "videotestsrc ! autovideosink";
		pipeline = (Pipeline) Gst.parseLaunch(pipeSpec);

		log.info("starting pipeline");
		pipeline.play();
	}

	@PreDestroy
	public void stop() {
		log.info("stopping pipeline");
		pipeline.stop();
	}
}
