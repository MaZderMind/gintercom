package de.mazdermind.gintercom.debugclient.pipeline;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.text.StringSubstitutor;
import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Gst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import de.mazdermind.gintercom.debugclient.pipeline.audiolevel.AudioLevelMessageListener;
import de.mazdermind.gintercom.shared.pipeline.StaticCaps;
import de.mazdermind.gintercom.shared.pipeline.support.PipelineStateChangeListener;

@Component
public class Pipeline {
	private static final Logger log = LoggerFactory.getLogger(Pipeline.class);

	private final PipelineStateChangeListener pipelineStateChangeListener;
	private final AudioLevelMessageListener levelMessageListener;
	private org.freedesktop.gstreamer.Pipeline pipeline;

	public Pipeline(
		@Autowired PipelineStateChangeListener pipelineStateChangeListener,
		@Autowired AudioLevelMessageListener levelMessageListener
	) {
		this.pipelineStateChangeListener = pipelineStateChangeListener;
		this.levelMessageListener = levelMessageListener;
	}

	@PostConstruct
	public void start() {
		log.info("initializing Gstreamer");
		Gst.init();

		log.info("creating pipeline");
		String pipelineSpec = StringSubstitutor.replace("" +
				"audiotestsrc freq=${test_freq} ! ${rawcaps} ! mix.\n" +
				"autoaudiosrc ! ${rawcaps} ! mix.\n" +
				"\n" +
				"audiomixer name=mix sink_0::volume=0.0 sink_1::volume=0.0 !\n" +
				"	${rawcaps} !\n" +
				"	audioconvert !\n" +
				"	${rawcaps_be} !\n" +
				"	rtpL16pay !\n" +
				"	${rtpcaps} !\n" +
				"	udpsink host=${matrix_host} port=${matrix_port}\n" +
				"\n" +
				"udpsrc port=${client_port} !\n" +
				"	${rtpcaps} !\n" +
				"	rtpjitterbuffer latency=50 !\n" +
				"	rtpL16depay !\n" +
				"	${rawcaps_be} !\n" +
				"	audioconvert !\n" +
				"	${rawcaps} !\n" +
				"	level name=audiolevel message=true interval=40000000 !\n" +
				"	volume name=speaker_volume volume=0.0 !\n" +
				"	autoaudiosink\n",
			ImmutableMap.<String, Object>builder()
				.put("test_freq", 440)
				.put("rtpcaps", StaticCaps.RTP)
				.put("rawcaps", StaticCaps.AUDIO)
				.put("rawcaps_be", StaticCaps.AUDIO_BE)
				.put("matrix_host", "127.0.0.1")
				.put("matrix_port", 20001)
				.put("client_port", 10001)
				.build()
		);

		log.info("Parsing Pipeline-Spec \n{}", pipelineSpec);
		pipeline = (org.freedesktop.gstreamer.Pipeline) Gst.parseLaunch(pipelineSpec);

		log.debug("Generating Debug-dot-File (if GST_DEBUG_DUMP_DOT_DIR Env-Variable is set)");
		pipeline.debugToDotFile(Bin.DebugGraphDetails.SHOW_ALL, "debug-client");

		pipeline.getBus().connect((Bus.EOS) pipelineStateChangeListener);
		pipeline.getBus().connect((Bus.STATE_CHANGED) pipelineStateChangeListener);
		pipeline.getBus().connect(levelMessageListener);

		log.info("starting pipeline");
		pipeline.play();
	}

	@PreDestroy
	public void stop() {
		log.info("stopping pipeline");
		pipeline.stop();
	}
}
