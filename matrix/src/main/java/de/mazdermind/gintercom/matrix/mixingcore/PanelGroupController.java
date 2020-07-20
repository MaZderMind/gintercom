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
import de.mazdermind.gintercom.mixingcore.Client;
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
		Client client = mixingCore.getClientByName(association.getHostId());

		Set<String> desiredRxGroups = panelGroupsChangedEvent.getRxGroups();
		Set<String> desiredTxGroups = panelGroupsChangedEvent.getTxGroups();
		reconcileGroups(client, desiredRxGroups, desiredTxGroups);
	}

	@VisibleForTesting
	void reconcileGroups(Client client, Set<String> desiredRxGroups, Set<String> desiredTxGroups) {
		log.debug("Reconciling rx-groups for {}", client.getName());
		calculateGroupsToRemove(client.getRxGroups(), desiredRxGroups)
			.forEach(client::stopReceivingFrom);

		calculateGroupsToAdd(client.getRxGroups(), desiredRxGroups)
			.forEach(client::startReceivingFrom);

		log.debug("Reconciling tx-groups for {}", client.getName());
		calculateGroupsToRemove(client.getTxGroups(), desiredTxGroups)
			.forEach(client::stopTransmittingTo);

		calculateGroupsToAdd(client.getTxGroups(), desiredTxGroups)
			.forEach(client::startTransmittingTo);
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
