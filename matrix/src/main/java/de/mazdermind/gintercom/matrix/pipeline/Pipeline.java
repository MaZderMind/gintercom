package de.mazdermind.gintercom.matrix.pipeline;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.Config;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelDeRegistrationEvent;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelRegistrationAware;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelRegistrationEvent;
import de.mazdermind.gintercom.shared.pipeline.support.PipelineStateChangeListener;

@Component
public class Pipeline implements PanelRegistrationAware {
	private static final Logger log = LoggerFactory.getLogger(Pipeline.class);
	private final Config config;
	private final PipelineStateChangeListener pipelineStateChangeListener;
	private final BeanFactory beanFactory;
	private org.freedesktop.gstreamer.Pipeline pipeline;

	private Map<String, Group> groups = new HashMap<>();
	private Map<String, Panel> panels = new HashMap<>();

	public Pipeline(
		@Autowired Config config,
		@Autowired PipelineStateChangeListener pipelineStateChangeListener,
		@Autowired BeanFactory beanFactory
	) {
		this.config = config;
		this.pipelineStateChangeListener = pipelineStateChangeListener;
		this.beanFactory = beanFactory;
	}

	@PostConstruct
	public void start() {
		log.info("initializing Gstreamer");
		Gst.init();

		log.info("creating pipeline");
		pipeline = new org.freedesktop.gstreamer.Pipeline("matrix");

		log.info("Creating Groups");
		config.getGroups().forEach((groupId, groupConfig) -> {
			Group group = beanFactory.getBean(Group.class);
			group.configure(pipeline, groupId, groupConfig);
			groups.put(groupId, group);
		});

/*
		config.getPanels().forEach((panelId, panelConfig) -> {
			Panel panel = beanFactory.getBean(Panel.class);
			panel.configure(pipeline, panelId, panelConfig);
			panels.put(panelId, panel);
		});
*/

		updateDotFile();

		pipeline.getBus().connect((Bus.EOS) pipelineStateChangeListener);
		pipeline.getBus().connect((Bus.STATE_CHANGED) pipelineStateChangeListener);

		log.info("starting pipeline");
		pipeline.play();
	}

	@PreDestroy
	public void stop() {
		log.info("stopping pipeline");
		pipeline.stop();
	}

	private void updateDotFile() {
		log.debug("Generating Debug-dot-File (if GST_DEBUG_DUMP_DOT_DIR Env-Variable is set)");
		pipeline.debugToDotFileWithTS(Bin.DebugGraphDetails.SHOW_ALL, "matrix");
	}

	@Override
	// TODO check if synchronized is required
	public synchronized void handlePanelRegistration(PanelRegistrationEvent event) {
		log.info("Creating Panel {}", event.getPanelId());
		Panel panel = beanFactory.getBean(Panel.class);
		panel.configure(pipeline, event.getPanelId(), event.getPanelConfig(), event.getPortSet(), event.getHostAddress());
		panels.put(event.getPanelId(), panel);
		updateDotFile();
	}

	@Override
	// TODO check if synchronized is required
	public synchronized void handlePanelDeRegistration(PanelDeRegistrationEvent event) {
		log.info("Removing Panel {}", event.getPanelId());
		Panel panel = panels.remove(event.getPanelId());
		panel.deconfigure();
		updateDotFile();
	}

	public State getState() {
		return pipeline.getState();
	}
}
