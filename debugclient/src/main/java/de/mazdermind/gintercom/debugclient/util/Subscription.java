package de.mazdermind.gintercom.debugclient.util;

public class Subscription {
	private final EventEmitter<?> eventEmitter;

	Subscription(EventEmitter<?> eventEmitter) {
		this.eventEmitter = eventEmitter;
	}

	public void unsubscribe() {
		eventEmitter.unsubscribe(this);
	}
}
