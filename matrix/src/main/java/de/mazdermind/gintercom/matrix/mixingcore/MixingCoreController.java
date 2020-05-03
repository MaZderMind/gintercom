package de.mazdermind.gintercom.matrix.mixingcore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelDeRegistrationEvent;
import de.mazdermind.gintercom.matrix.controlserver.panelregistration.PanelRegistrationEvent;
import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Panel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MixingCoreController {
	private final MixingCore mixingCore;

	public MixingCoreController(
		@Autowired MixingCore mixingCore
	) {
		this.mixingCore = mixingCore;
	}

	@EventListener
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

	@EventListener
	public void handlePanelDeRegistration(PanelDeRegistrationEvent panelDeRegistrationEvent) {
		log.info("DeRegistering Panel {}", panelDeRegistrationEvent.getPanelId());
		Panel panel = mixingCore.getPanelByName(panelDeRegistrationEvent.getPanelId());
		mixingCore.removePanel(panel);
	}
}
