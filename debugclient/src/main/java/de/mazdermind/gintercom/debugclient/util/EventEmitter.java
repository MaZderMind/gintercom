package de.mazdermind.gintercom.debugclient.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EventEmitter<T> {
	private Map<Subscription, Consumer<T>> subscriptions = new HashMap<>();

	public void emit(T event) {
		subscriptions.values().forEach(eventHandler -> eventHandler.accept(event));
	}

	public Subscription subscribe(Consumer<T> eventHandler) {
		Subscription subscription = new Subscription(this);
		subscriptions.put(subscription, eventHandler);
		return subscription;
	}

	public void unsubscribe(Subscription subscription) {
		subscriptions.remove(subscription);
	}
}
