package de.mazdermind.gintercom.matrix.webui;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UiUpdateService {
	private final SimpMessageSendingOperations simpMessageSendingOperations;

	public void notifyUiAboutUpdates() {
		log.info("notifyUiAboutUpdates");
		simpMessageSendingOperations.convertAndSend("/ui/update", ImmutableMap.of("type", "update"));
	}

	@EventListener
	public void onUiUpdateEvent(UiUpdateEvent updateEvent) {
		notifyUiAboutUpdates();
	}
}
