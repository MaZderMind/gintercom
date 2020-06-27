package de.mazdermind.gintercom.matrix.mixingcore;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import de.mazdermind.gintercom.matrix.controlserver.ClientAssociation;
import de.mazdermind.gintercom.matrix.events.PanelGroupsChangedEvent;
import de.mazdermind.gintercom.mixingcore.Group;
import de.mazdermind.gintercom.mixingcore.MixingCore;
import de.mazdermind.gintercom.mixingcore.Panel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PanelGroupController {
	private final MixingCore mixingCore;

	@EventListener
	public void handlePanelConfigurationChangedEvent(PanelGroupsChangedEvent panelGroupsChangedEvent) {
		ClientAssociation association = panelGroupsChangedEvent.getAssociation();
		Panel panel = mixingCore.getPanelByName(association.getHostId());

		Set<String> desiredRxGroups = panelGroupsChangedEvent.getRxGroups();
		Set<String> desiredTxGroups = panelGroupsChangedEvent.getTxGroups();
		reconcileGroups(panel, desiredRxGroups, desiredTxGroups);
	}

	@VisibleForTesting
	void reconcileGroups(Panel panel, Set<String> desiredRxGroups, Set<String> desiredTxGroups) {
		log.debug("Reconciling rx-groups for {}", panel.getName());
		calculateGroupsToRemove(panel.getRxGroups(), desiredRxGroups)
			.forEach(panel::stopReceivingFrom);

		calculateGroupsToAdd(panel.getRxGroups(), desiredRxGroups)
			.forEach(panel::startReceivingFrom);

		log.debug("Reconciling tx-groups for {}", panel.getName());
		calculateGroupsToRemove(panel.getTxGroups(), desiredTxGroups)
			.forEach(panel::stopTransmittingTo);

		calculateGroupsToAdd(panel.getTxGroups(), desiredTxGroups)
			.forEach(panel::startTransmittingTo);
	}

	@VisibleForTesting
	Set<Group> calculateGroupsToAdd(Set<Group> actualGroups, Set<String> desiredGroups) {
		Set<String> actualGroupNames = actualGroups.stream()
			.map(Group::getName)
			.collect(Collectors.toSet());

		return desiredGroups.stream()
			.filter(desiredGroup -> !actualGroupNames.contains(desiredGroup))
			.map(mixingCore::getGroupByName)
			.collect(Collectors.toSet());
	}

	@VisibleForTesting
	Set<Group> calculateGroupsToRemove(Set<Group> actualGroups, Set<String> desiredGroups) {
		return actualGroups.stream()
			.filter(actualGroup -> !desiredGroups.contains(actualGroup.getName()))
			.collect(Collectors.toSet());
	}
}
