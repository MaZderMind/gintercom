package de.mazdermind.gintercom.matrix.integration.tools.controlserver;

import java.util.concurrent.CompletableFuture;

class FutureMessage {
	private String destination;
	private CompletableFuture<Object> future;

	FutureMessage(String destination) {
		this.destination = destination;
		future = new CompletableFuture<>();
	}

	String getDestination() {
		return destination;
	}

	CompletableFuture<Object> getFuture() {
		return future;
	}
}
