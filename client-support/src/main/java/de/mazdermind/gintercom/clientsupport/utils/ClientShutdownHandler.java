package de.mazdermind.gintercom.clientsupport.utils;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import de.mazdermind.gintercom.clientsupport.events.BeforeClientShutdownEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientShutdownHandler implements SmartLifecycle {
	private final ApplicationEventPublisher eventPublisher;

	private boolean started = true;

	@Override
	public void start() {
		started = true;
	}

	@Override
	public void stop() {
		log.info("Application Shutdown");
		eventPublisher.publishEvent(new BeforeClientShutdownEvent());
	}

	@Override
	public boolean isRunning() {
		return started;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

	@Override
	public int getPhase() {
		return Integer.MAX_VALUE;
	}
}
