package de.mazdermind.gintercom.shared.pipeline.support;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.freedesktop.gstreamer.Gst;

public class GstInvoker {
	public static void invokeLater(Runnable runnable) {
		Gst.invokeLater(runnable);
	}

	public static void invokeAndWait(Runnable runnable) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		Gst.invokeLater(() -> {
			runnable.run();
			future.complete(null);
		});

		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
