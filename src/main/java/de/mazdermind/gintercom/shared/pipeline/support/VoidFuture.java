package de.mazdermind.gintercom.shared.pipeline.support;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
			future.get(1, TimeUnit.MINUTES);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}
}
