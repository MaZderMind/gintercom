package de.mazdermind.gintercom.shared.pipeline.support;

import org.freedesktop.gstreamer.StateChangeReturn;

public class GstErrorCheck {
	public static void expectTrue(boolean returnValue) {
		if (!returnValue) {
			throw new GstError(String.format("Expected a return-value of true, was %s", returnValue));
		}
	}

	public static void expectAsyncOrSuccess(StateChangeReturn stateChangeReturn) {
		if (stateChangeReturn != StateChangeReturn.SUCCESS && stateChangeReturn != StateChangeReturn.ASYNC) {
			throw new GstError(String.format("Expected a return-value of SUCCESS or ASYNC, was %s", stateChangeReturn));
		}
	}

	public static void expectSuccess(StateChangeReturn stateChangeReturn) {
		if (stateChangeReturn != StateChangeReturn.SUCCESS) {
			throw new GstError(String.format("Expected a return-value of SUCCESS, was %s", stateChangeReturn));
		}
	}

	public static void expectNull(Object o) {
		if (o != null) {
			throw new GstError(String.format("Expected a return-value of null, was %s", o));
		}
	}

	private static class GstError extends RuntimeException {
		private final String message;

		GstError(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return message;
		}
	}
}
