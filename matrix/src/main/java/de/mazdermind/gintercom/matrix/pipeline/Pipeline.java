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

	@Override
	public synchronized void handlePanelRegistration(PanelRegistrationEvent event) {
		log.info("Configuring Panel {}", event.getPanelId());
		Gst.invokeLater(() -> {
			Panel panel = beanFactory.getBean(Panel.class);
			panel.configure(pipeline, event.getPanelId(), event.getPanelConfig(), event.getPortSet(), event.getHostAddress());
			panels.put(event.getPanelId(), panel);
			pipeline.debugToDotFileWithTS(Bin.DebugGraphDetails.SHOW_ALL, String.format("panel-%s-configure", event.getPanelId()));

			log.info("Linking Panel {} to configured Rx/Tx Groups", event.getPanelId());
			event.getPanelConfig().getRxGroups().forEach(groupName -> panel.startReceivingFromGroup(groups.get(groupName)));
			event.getPanelConfig().getTxGroups().forEach(groupName -> panel.startTransmittingToGroup(groups.get(groupName)));
			pipeline.debugToDotFileWithTS(Bin.DebugGraphDetails.SHOW_ALL, String.format("panel-%s-link-static-groups", event.getPanelId()));
		});
	}

	@Override
	public synchronized void handlePanelDeRegistration(PanelDeRegistrationEvent event) {
		log.info("Deconfiguring Panel {}", event.getPanelId());
		Panel panel = panels.remove(event.getPanelId());
		Gst.invokeLater(() -> {
			panel.deconfigure();
			pipeline.debugToDotFileWithTS(Bin.DebugGraphDetails.SHOW_ALL, String.format("panel-%s-deconfigure", event.getPanelId()));
		});
	}

	public State getState() {
		return pipeline.getState();
	}
}
