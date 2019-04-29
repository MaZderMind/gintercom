package de.mazdermind.gintercom.matrix.pipeline;

import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.PanelConfig;
import de.mazdermind.gintercom.shared.pipeline.StaticCaps;

@Component
@Scope("prototype")
public class Panel {
	private static Logger log = LoggerFactory.getLogger(Panel.class);

	private final PanelReceivePath panelReceivePath;
	private final PanelTransmitPath panelTransmitPath;

	public Panel(
		@Autowired PanelReceivePath panelReceivePath,
		@Autowired PanelTransmitPath panelTransmitPath
	) {
		this.panelReceivePath = panelReceivePath;
		this.panelTransmitPath = panelTransmitPath;
	}

	public void configure(Pipeline pipeline, String panelId, PanelConfig panelConfig) {
		log.info("Creating Pipeline-Elements for Panel {}", panelId);
		panelReceivePath.configure(pipeline, panelId, panelConfig);
		panelTransmitPath.configure(pipeline, panelId, panelConfig);

		linkRxGroups(pipeline, panelId, panelConfig);
	}

	private void linkRxGroups(Pipeline pipeline, String panelId, PanelConfig panelConfig) {
		log.info("Linking Panel {} to Rx-Groups {}", panelId, panelConfig.getRxGroups());
		panelConfig.getRxGroups().forEach(rxGroup -> {
			Element groupTee = pipeline.getElementByName(String.format("group-tee-%s", rxGroup));
			Element panelMixer = pipeline.getElementByName(String.format("panel-tx-%s", panelId));

			boolean success = Element.linkPadsFiltered(groupTee, "src_%u", panelMixer, "sink_%u", StaticCaps.AUDIO);
			if (!success) {
				log.error("Link unsuccessful");
			}
		});

		log.info("Linking Panel {} to Tx-Groups {}", panelId, panelConfig.getTxGroups());
		panelConfig.getRxGroups().forEach(txGroup -> {
			Element panelTee = pipeline.getElementByName(String.format("panel-rx-%s", panelId));
			Element groupMixer = pipeline.getElementByName(String.format("group-mixer-%s", txGroup));
			boolean success = Element.linkPadsFiltered(panelTee, "src_%u", groupMixer, "sink_%u", StaticCaps.AUDIO);

			if (!success) {
				log.error("Link unsuccessful");
			}
		});
	}
}
