package de.mazdermind.gintercom.mixingcore.support;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.freedesktop.gstreamer.Pad;

public class GstPadBlock {
	/**
	 * Run a supplier (a callback which returns a value) under a blocked state and wait *up to timeout* for the runnable to complete. Return
	 * the value to the waiting thread.
	 * The pad will remain blocked for as long as the callback is active.
	 * The callback may be called from this thread or another thread.
	 *
	 * @param supplier The code to run when pad is blocked
	 */
	public static <T> T blockAndWait(Pad pad, Supplier<T> supplier, Duration timeout) {
		CompletableFuture<T> future = new CompletableFuture<>();
		pad.block(() -> {
			T result = supplier.get();
			future.complete(result);
		});

		try {
			return future.get(timeout.getSeconds(), TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Run a supplier (a callback which returns a value) under a blocked state and wait *indefinitely* for the runnable to complete. Return
	 * the value to the waiting thread.
	 * The pad will remain blocked for as long as the callback is active.
	 * The callback may be called from this thread or another thread.
	 *
	 * @param supplier The code to run when pad is blocked
	 */
	public static <T> T blockAndWait(Pad pad, Supplier<T> supplier) {
		CompletableFuture<T> future = new CompletableFuture<>();
		pad.block(() -> {
			T result = supplier.get();
			future.complete(result);
		});

		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Run a runnable under a blocked state and wait *up to timeout* for the runnable to complete.
	 * The pad will remain blocked for as long as the callback is active.
	 * The callback may be called from this thread or another thread.
	 *
	 * @param callback The code to run when pad is blocked
	 */
	public static void blockAndWait(Pad pad, Runnable callback, Duration timeout) {
		blockAndWait(pad, () -> {
			callback.run();
			return null;
		}, timeout);
	}

	/**
	 * Run a runnable under a blocked state and wait *indefinitely for the runnable to complete.
	 * The pad will remain blocked for as long as the callback is active.
	 * The callback may be called from this thread or another thread.
	 *
	 * @param callback The code to run when pad is blocked
	 */
	public static void blockAndWait(Pad pad, Runnable callback) {
		blockAndWait(pad, () -> {
			callback.run();
			return null;
		});
	}
}
