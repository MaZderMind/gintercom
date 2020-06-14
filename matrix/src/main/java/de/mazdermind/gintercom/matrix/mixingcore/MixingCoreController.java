package de.mazdermind.gintercom.matrix.mixingcore;

import javax.annotation.PreDestroy;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.events.PanelAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.PanelDeAssociatedEvent;
import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Panel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MixingCoreController {
	private final MixingCore mixingCore;

	@EventListener
	public void handlePanelRegistration(PanelAssociatedEvent panelAssociatedEvent) {
		log.debug("Registering Panel {}", panelAssociatedEvent.getPanelId());
		Panel panel = mixingCore.addPanel(
			panelAssociatedEvent.getPanelId(),
			panelAssociatedEvent.getAssociation().getSocketAddress().getAddress(),
			panelAssociatedEvent.getAssociation().getRtpPorts().getPanelToMatrix(),
			panelAssociatedEvent.getAssociation().getRtpPorts().getMatrixToPanel()
		);

		log.debug("Configuring initial Group-Membership");
		panelAssociatedEvent.getPanelConfig().getRxGroups().forEach(groupId -> {
			Group group = mixingCore.getGroupByName(groupId);
			panel.startReceivingFrom(group);
		});

		panelAssociatedEvent.getPanelConfig().getTxGroups().forEach(groupId -> {
			Group group = mixingCore.getGroupByName(groupId);
			panel.startTransmittingTo(group);
		});
	}

	@EventListener
	public void handlePanelDeRegistration(PanelDeAssociatedEvent panelDeAssociatedEvent) {
		log.debug("DeRegistering Panel {}", panelDeAssociatedEvent.getPanelId());
		Panel panel = mixingCore.getPanelByName(panelDeAssociatedEvent.getPanelId());
		mixingCore.removePanel(panel);
	}

	@PreDestroy
	public void destroyPipeline() {
		log.info("Destroying MixingCore");
		mixingCore.destroy();
	}
}
