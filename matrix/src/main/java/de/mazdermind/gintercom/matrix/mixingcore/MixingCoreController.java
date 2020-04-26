package de.mazdermind.gintercom.matrix.mixingcore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelDeRegistrationEvent;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelRegistrationAware;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelRegistrationEvent;
import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Panel;

@Component
public class MixingCoreController implements PanelRegistrationAware {
	private static final Logger log = LoggerFactory.getLogger(MixingCoreController.class);
	private final MixingCore mixingCore;

	public MixingCoreController(
		@Autowired MixingCore mixingCore
	) {
		this.mixingCore = mixingCore;
	}

	@Override
	public void handlePanelRegistration(PanelRegistrationEvent panelRegistrationEvent) {
		log.info("Registering Panel {}", panelRegistrationEvent.getPanelId());
		Panel panel = mixingCore.addPanel(
			panelRegistrationEvent.getPanelId(),
			panelRegistrationEvent.getHostAddress(),
			panelRegistrationEvent.getPortSet().getPanelToMatrix(),
			panelRegistrationEvent.getPortSet().getMatrixToPanel()
		);

		log.info("Configuring initial Group-Membership");
		panelRegistrationEvent.getPanelConfig().getRxGroups().forEach(groupId -> {
			Group group = mixingCore.getGroupByName(groupId);
			panel.startReceivingFrom(group);
		});

		panelRegistrationEvent.getPanelConfig().getTxGroups().forEach(groupId -> {
			Group group = mixingCore.getGroupByName(groupId);
			panel.startTransmittingTo(group);
		});
	}

	@Override
	public void handlePanelDeRegistration(PanelDeRegistrationEvent panelDeRegistrationEvent) {
		log.info("DeRegistering Panel {}", panelDeRegistrationEvent.getPanelId());
		Panel panel = mixingCore.getPanelByName(panelDeRegistrationEvent.getPanelId());
		mixingCore.removePanel(panel);
	}
}
