package de.mazdermind.gintercom.matrix.webui;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import de.mazdermind.gintercom.matrix.events.ClientAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.ClientDeAssociatedEvent;
import de.mazdermind.gintercom.matrix.events.GroupsChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UiUpdateService {
	private final SimpMessageSendingOperations simpMessageSendingOperations;

	@EventListener({
		ClientAssociatedEvent.class, ClientDeAssociatedEvent.class,
		GroupsChangedEvent.class,
	})
	public void notifyUiAboutUpdates() {
		log.info("Updating UI");
		simpMessageSendingOperations.convertAndSend("/ui/update", ImmutableMap.of("type", "update"));
	}
}
