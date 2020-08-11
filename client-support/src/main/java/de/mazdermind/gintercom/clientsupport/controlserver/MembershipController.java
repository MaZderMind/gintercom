package de.mazdermind.gintercom.clientsupport.controlserver;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.clientapi.configuration.CommunicationTargetType;
import de.mazdermind.gintercom.clientapi.controlserver.messages.client.to.matrix.MembershipChangeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MembershipController {
	private final ClientMessageSender messageSender;

	private final Set<Membership> activeMemberships = new HashSet<>();

	public void changeMembership(MembershipChangeMessage.Change change, CommunicationTargetType targetType, String target) {
		Membership membership = new Membership(targetType, target);
		if (change == MembershipChangeMessage.Change.JOIN) {
			activeMemberships.add(membership);
		} else if (change == MembershipChangeMessage.Change.LEAVE) {
			activeMemberships.remove(membership);
		}

		sendMembershipChangeMessage(change, membership);
	}

	private void sendMembershipChangeMessage(MembershipChangeMessage.Change change, Membership membership) {
		sendMembershipChangeMessage(new MembershipChangeMessage()
			.setChange(change)
			.setTargetType(membership.getTargetType())
			.setTarget(membership.getTarget()));
	}

	private void sendMembershipChangeMessage(MembershipChangeMessage message) {
		log.info("Sending MembershipChangeMessage {}", message);
		messageSender.sendMessage(message);
	}

	public void leaveAllJoined() {
		if (!activeMemberships.isEmpty()) {
			log.info("Sending a LEAVE-Message for all active {} Memberships", activeMemberships.size());

			activeMemberships.forEach(membership -> sendMembershipChangeMessage(MembershipChangeMessage.Change.LEAVE, membership));
			activeMemberships.clear();
		}
	}

	@Data
	@AllArgsConstructor
	private static class Membership {
		private CommunicationTargetType targetType;
		private String target;
	}
}
