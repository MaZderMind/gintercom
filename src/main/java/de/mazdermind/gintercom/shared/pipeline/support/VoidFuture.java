package de.mazdermind.gintercom.shared.pipeline.support;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VoidFuture {

	private final CompletableFuture<Void> future;

	public VoidFuture() {
		future = new CompletableFuture<>();
	}

	public void complete() {
		future.complete(null);
	}

	public void await() {
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
