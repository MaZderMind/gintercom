package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

import static de.mazdermind.gintercom.shared.utils.ObjectListClassNameUtil.classNamesList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.connection.ControlServerSessionTransportErrorMulticaster;

@Component
public class PanelRegistrationAwareMulticaster {
	private static final Logger log = LoggerFactory.getLogger(ControlServerSessionTransportErrorMulticaster.class);
	private final List<PanelRegistrationAware> panelRegistrationAwares;

	public PanelRegistrationAwareMulticaster(
		@Autowired List<PanelRegistrationAware> panelRegistrationAwares
	) {
		this.panelRegistrationAwares = panelRegistrationAwares;

		log.info("Found {} PanelRegistrationAware Implementations: {}", panelRegistrationAwares
			.size(), classNamesList(panelRegistrationAwares));
	}

	public void dispatchPanelRegistration(PanelRegistrationEvent panelRegistrationEvent) {
		panelRegistrationAwares.forEach(panelRegistrationAware -> {
			panelRegistrationAware.handlePanelRegistration(panelRegistrationEvent);
		});
	}

	public void dispatchPanelDeRegistration(PanelDeRegistrationEvent panelDeRegistrationEvent) {
		panelRegistrationAwares.forEach(panelRegistrationAware -> {
			panelRegistrationAware.handlePanelDeRegistration(panelDeRegistrationEvent);
		});
	}
}
